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
    val isRecommended: Boolean = true,
    val topResults: List<Pair<String, Float>> = emptyList() // (label, prob)
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

    var isInitialised = false

    // --- Model and Parameter Loading ---
    init {
        try {
            // Load the model and all parameters needed for inference
            val tflitePath = context.getString(R.string.model_file_tflite)
            val labelsPath = context.getString(R.string.model_file_labels)
            val scalerPath = context.getString(R.string.model_file_scaler)

            tflite = loadModelFile(tflitePath)
            loadLabels(labelsPath)
            loadScalerParams(scalerPath)

            isInitialised = true
        } catch (e: Exception) {
            // Log the error and prevent the app from crashing entirely
            // In a real app, you would show a user-friendly error dialog.
            println("FATAL ERROR: Failed to initialize TFLite model or parameters: $e")
            isInitialised = false
        }
    }

    // In CropRecommender.kt, replace the loadModelFile function

    private fun loadModelFile(assetPath: String): Interpreter {
        val assetManager = context.assets

        // Read into a ByteArray & then into a direct, native-order ByteBuffer
        val buffer = assetManager.open(assetPath).use { inputStream ->
            val fileBytes = inputStream.readBytes()

            java.nio.ByteBuffer
                .allocateDirect(fileBytes.size)
                .order(java.nio.ByteOrder.nativeOrder()) // ✅ IMPORTANT
                .apply {
                    put(fileBytes)
                    rewind() // reset position to 0
                }
        }

        val options = Interpreter.Options().apply {
            setNumThreads(2)
        }

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
        val rawInputs = FloatArray(featureNames.size) { idx ->
            val modelName = featureNames[idx]          // Name from JSON / Python
            val mapKey = modelName.replace('-', '_')   // Name used in Kotlin maps

            features[mapKey]
                ?: throw IllegalStateException("Missing feature: $modelName (mapped key: $mapKey)")
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

        val topK = 3
        val topIndices = probabilities.indices
            .sortedByDescending { probabilities[it] }
            .take(topK)

        val topResults = topIndices.map { idx ->
            labels[idx] to probabilities[idx]
        }

// In recommendation mode:
        if (selectedValidationCrop.isNullOrEmpty()) {
            val predictedIndex = topIndices.firstOrNull() ?: return CropPrediction("Error", 0f, false, topResults)
            val confidence = probabilities[predictedIndex]
            return CropPrediction(labels[predictedIndex], confidence, true, topResults)
        } else {
            // validation branch (keep same behavior for header), but still fill topResults
            val cropIndex = labels.indexOf(selectedValidationCrop)
            if (cropIndex == -1) {
                return CropPrediction("Unknown Crop", 0f, false, topResults)
            }
            val confidence = probabilities[cropIndex]
            return CropPrediction(
                predictedCrop = selectedValidationCrop,
                confidence = confidence,
                isRecommended = confidence > 0.45f,
                topResults = topResults
            )
        }
    }
}