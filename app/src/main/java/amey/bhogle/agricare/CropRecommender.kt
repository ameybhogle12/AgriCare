package amey.bhogle.agricare

import android.content.Context
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import com.google.gson.Gson

// Helper structure to hold the prediction result
data class CropPrediction(
    val predictedCrop: String,
    val confidence: Float,
    val isRecommended: Boolean = true
)

// Kotlin data class to map the structure of the scaler_params_v3.json file
// Use Double for JSON parsing as it is the default representation for numbers
private data class ScalerParams(
    val mean: List<Double>,
    val std: List<Double>,
    val feature_names: List<String>
)

class CropRecommender(private val context: Context) {

    private lateinit var tflite: Interpreter
    private lateinit var labels: List<String>
    private lateinit var scalerMeans: FloatArray
    private lateinit var scalerStds: FloatArray
    private lateinit var featureNames: List<String>

    // --- Model and Parameter Loading ---
    init {
        try {
            // Load the model and all parameters needed for inference
            tflite = loadModelFile("crop_recommender_v3.tflite")
            loadLabels("labels_v3.txt")
            loadScalerParams("scaler_params_v3.json")
        } catch (e: Exception) {
            // Log the error and prevent the app from crashing entirely
            // In a real app, you would show a user-friendly error dialog.
            println("FATAL ERROR: Failed to initialize TFLite model or parameters: $e")
        }
    }

    private fun loadModelFile(assetPath: String): Interpreter {
        // Loads the TFLite file from assets folder into a memory-mapped buffer
        val fileDescriptor = context.assets.openFd(assetPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val buffer: MappedByteBuffer = fileChannel.map(
            FileChannel.MapMode.READ_ONLY, startOffset, declaredLength
        )
        // Set number of threads for optimal performance (using 2-4 is often best for CPU)
        val options = Interpreter.Options()
        options.setNumThreads(2)
        return Interpreter(buffer, options)
    }

    private fun loadLabels(assetPath: String) {
        // Loads the crop names
        labels = context.assets.open(assetPath).bufferedReader().useLines { it.toList() }
    }

    private fun loadScalerParams(assetPath: String) {
        // Loads the mean and standard deviation arrays from JSON
        val jsonString = context.assets.open(assetPath).bufferedReader().use { it.readText() }
        val gson = Gson()
        val params = gson.fromJson(jsonString, ScalerParams::class.java)

        // Convert List<Double> from JSON to FloatArray for TFLite math
        scalerMeans = params.mean.map { it.toFloat() }.toFloatArray()
        scalerStds = params.std.map { it.toFloat() }.toFloatArray()
        featureNames = params.feature_names

        if (featureNames.size != 10) {
            throw IllegalStateException("Model expects 10 features, but loaded ${featureNames.size}.")
        }
    }

    // --- Core Logic: Standardization Function (The crucial math) ---
    private fun standardizeFeatures(rawInputs: FloatArray): FloatArray {
        if (rawInputs.size != 10) throw IllegalArgumentException("Expected 10 features.")

        val standardized = FloatArray(10)
        for (i in rawInputs.indices) {
            // Z-Score Normalization: (X - mean) / std_dev
            standardized[i] = (rawInputs[i] - scalerMeans[i]) / scalerStds[i]
        }
        return standardized
    }

    // --- Main Prediction Functions ---

    fun getPrediction(
        selectedRegion: String,
        selectedSoil: String,
        selectedWeather: String,
        selectedValidationCrop: String? = null
    ): CropPrediction {

        // 1. Map UI Inputs to 10 Raw Features

        val soilBase = soilTypeMap[selectedSoil] ?: return CropPrediction("Invalid Soil", 0f, false)

        // Convert to mutable map for modification
        val features = mutableMapOf(
            "N" to soilBase.N, "P" to soilBase.P, "K" to soilBase.K,
            "Ph" to soilBase.Ph, "S" to soilBase.S, "Zn" to soilBase.Zn,
            "PRECTOTCORR_Su" to soilBase.PRECTOTCORR_Su, "T2M_MAX_Sp" to soilBase.T2M_MAX_Sp,
            "T2M_MIN_W" to soilBase.T2M_MIN_W, "QV2M_Su" to soilBase.QV2M_Su
        )

        // Overwrite baseline weather with Region-specific values
        regionWeatherMap[selectedRegion]?.let { regionData ->
            regionData.forEach { (key, value) ->
                if (features.containsKey(key)) {
                    features[key] = value
                }
            }
        } ?: return CropPrediction("Invalid Region", 0f, false)

        // Apply seasonal adjustments (Monsoon, Winter, Summer)
        applySeasonRefinement(features, selectedWeather)


        // 2. Standardize the 10 Features

        // Ensure order matches the model (feature_names list from JSON)
        val rawInputs = FloatArray(10) {
            features[featureNames[it]] ?: throw IllegalStateException("Missing feature: ${featureNames[it]}")
        }

        val standardizedInputs = standardizeFeatures(rawInputs)

        // 3. Run TFLite Inference

        // Input buffer (1 row, 10 columns, Float32)
        val inputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 10), DataType.FLOAT32)
        inputBuffer.loadArray(standardizedInputs, intArrayOf(1, 10))

        // Output buffer (1 row, NUM_CLASSES, Float32)
        val numClasses = labels.size
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, numClasses), DataType.FLOAT32)

        tflite.run(inputBuffer.buffer, outputBuffer.buffer)

        val probabilities = outputBuffer.floatArray

        // 4. Post-Process (Recommendation OR Validation)

        return if (selectedValidationCrop.isNullOrEmpty()) {
            // A. RECOMMENDATION MODE: Find the highest probability
            val predictedIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: return CropPrediction("Error", 0f, false)
            val confidence = probabilities[predictedIndex]
            CropPrediction(labels[predictedIndex], confidence)

        } else {
            // B. VALIDATION MODE: Check confidence for the farmer's chosen crop
            val cropIndex = labels.indexOf(selectedValidationCrop)
            if (cropIndex == -1) {
                return CropPrediction("Unknown Crop", 0f, false)
            }
            val confidence = probabilities[cropIndex]

            // Threshold: Define 45% confidence as "Viable"
            CropPrediction(
                predictedCrop = selectedValidationCrop,
                confidence = confidence,
                isRecommended = confidence > 0.45f
            )
        }
    }
}