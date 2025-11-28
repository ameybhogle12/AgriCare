package amey.bhogle.agricare

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSuggestions(navController: NavController) {
    // your existing lists (keep labels consistent)
    val regionOptions = regionWeatherMap.keys.toList()
    val soilOptionsAll = soilTypeMap.keys.toList()
    val weatherOptions = listOf("Kharif (Monsoon)", "Rabi (Winter)", "Zaid (Summer)")
    val cropOptions = listOf("Apple", "Banana", "Coffee", "Jute", "Mango", "Maize", "Orange", "Rice", "Wheat")

    // map regions -> allowed soils (tweak as you see fit)
    val regionToSoils = mapOf(
        "Himalayan Region (Cold)" to listOf("Alluvial Soil", "Red Soil"),
        "Northern Plains (Hot/Cold)" to listOf("Alluvial Soil", "Black (Regur) Soil"),
        "Deccan Plateau (Semi-Arid)" to listOf("Black (Regur) Soil", "Red Soil", "Laterite Soil"),
        "Coastal South (Humid)" to listOf("Laterite Soil", "Red Soil", "Alluvial Soil"),
        "Arid Zone (Rajasthan)" to listOf("Desert Soil", "Alluvial Soil")
    )

    val context = LocalContext.current
    val recommender = remember { CropRecommender(context) }

    var selectedRegion by remember { mutableStateOf("") }
    var expandedRegion by remember { mutableStateOf(false) }

    // soil is now dynamic based on selectedRegion
    var selectedSoil by remember { mutableStateOf("") }
    var expandedSoil by remember { mutableStateOf(false) }

    var selectedWeather by remember { mutableStateOf("") }
    var expandedWeather by remember { mutableStateOf(false) }

    // By default, disable validation (experimental). User can enable to test at their own risk.
    var enableValidation by remember { mutableStateOf(false) } // toggle: false = validation disabled
    var selectedCrop by remember { mutableStateOf("") }
    var expandedCrop by remember { mutableStateOf(false) }

    var predictionResultText by remember { mutableStateOf("Suggestions here...") }
    var predictionResultColor by remember { mutableStateOf(Color.Gray) }

    Scaffold(
        topBar = { AppTopBar(navController, title = "Smart Suggestions") },
        bottomBar = {
            BottomNavBar(selectedItem = "Suggest") { selected ->
                when (selected) {
                    "Home" -> navController.navigate("home")
                    "Suggest" -> navController.navigate("smart_suggestions")
                    "Language" -> navController.navigate("language")
                    "Help" -> navController.navigate("help")
                    "Settings" -> navController.navigate("settings")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "BackGround Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                // --- Region selector ---
                ExposedDropdownMenuBox(
                    expanded = expandedRegion,
                    onExpandedChange = { expandedRegion = !expandedRegion }
                ) {
                    TextField(
                        value = selectedRegion,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Region") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRegion) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(0.9f),
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedLabelColor = Color(0xFF4CAF50),
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRegion,
                        onDismissRequest = { expandedRegion = false }
                    ) {
                        regionOptions.forEach { region ->
                            DropdownMenuItem(
                                text = { Text(region) },
                                onClick = {
                                    selectedRegion = region
                                    expandedRegion = false
                                    // auto-select allowed soil when region changes
                                    val allowed = regionToSoils[region] ?: soilOptionsAll
                                    if (!allowed.contains(selectedSoil)) {
                                        selectedSoil = allowed.first()
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Soil selector (filtered by region) ---
                ExposedDropdownMenuBox(
                    expanded = expandedSoil,
                    onExpandedChange = { expandedSoil = !expandedSoil }
                ) {
                    TextField(
                        value = selectedSoil,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Soil Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSoil) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(0.9f),
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedLabelColor = Color(0xFF4CAF50),
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSoil,
                        onDismissRequest = { expandedSoil = false }
                    ) {
                        val allowedSoils = regionToSoils[selectedRegion] ?: soilOptionsAll
                        allowedSoils.forEach { soil ->
                            DropdownMenuItem(
                                text = { Text(soil) },
                                onClick = {
                                    selectedSoil = soil
                                    expandedSoil = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Weather selector ---
                ExposedDropdownMenuBox(
                    expanded = expandedWeather,
                    onExpandedChange = { expandedWeather = !expandedWeather }
                ) {
                    TextField(
                        value = selectedWeather,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Weather") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWeather) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(0.9f),
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            focusedLabelColor = Color(0xFF4CAF50),
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedWeather,
                        onDismissRequest = { expandedWeather = false }
                    ) {
                        weatherOptions.forEach { weather ->
                            DropdownMenuItem(
                                text = { Text(weather) },
                                onClick = {
                                    selectedWeather = weather
                                    expandedWeather = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- Validation toggle & Crop selector (disabled unless toggled) ---
                // Simple toggle: when not enabled, crop remains visible but disabled with hint.
                Text(
                    text = if (enableValidation) "Validation (Experimental) — Enabled" else "Validation (Experimental) — Disabled",
                    fontSize = 12.sp,
                    color = if (enableValidation) Color(0xFF4CAF50) else Color.DarkGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCrop,
                    onExpandedChange = { if (enableValidation) expandedCrop = !expandedCrop }
                ) {
                    TextField(
                        value = if (enableValidation) selectedCrop else "Validation disabled",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Crop (Optional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCrop) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth(0.9f),
                        enabled = enableValidation,
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color(0xFFF1F1F1),
                            disabledContainerColor = Color(0xFFF1F1F1),
                            focusedLabelColor = Color(0xFF4CAF50),
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    if (enableValidation) {
                        ExposedDropdownMenu(
                            expanded = expandedCrop,
                            onDismissRequest = { expandedCrop = false }
                        ) {
                            cropOptions.forEach { crop ->
                                DropdownMenuItem(
                                    text = { Text(crop) },
                                    onClick = {
                                        selectedCrop = crop
                                        expandedCrop = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Predict Button
                ElevatedButton(
                    onClick = {
                        if (!recommender.isInitialised) {
                            predictionResultText = "ERROR: AI model files failed to load. Check your assets folder!"
                            predictionResultColor = Color.Red
                            return@ElevatedButton
                        }
                        if (selectedRegion.isEmpty() || selectedSoil.isEmpty() || selectedWeather.isEmpty()) {
                            predictionResultText = "Please select Region, Soil, and Season to predict."
                            predictionResultColor = Color.Red
                            return@ElevatedButton
                        }

                        val prediction = recommender.getPrediction(
                            selectedRegion,
                            selectedSoil,
                            selectedWeather,
                            if (enableValidation) selectedCrop.ifEmpty { null } else null
                        )

                        if (enableValidation && selectedCrop.isNotEmpty()) {
                            val confidencePercent = prediction.confidence * 100
                            val viability = if (prediction.isRecommended) "Viable" else "Not Recommended"
                            predictionResultColor = if (prediction.isRecommended) Color(0xFF4CAF50) else Color.Red
                            predictionResultText = "VIABILITY CHECK for ${prediction.predictedCrop}:\nConfidence: ${confidencePercent.toFormattedString()}%\nResult: $viability\n\nTop suggestions:\n" +
                                    prediction.topResults.joinToString("\n") { "${it.first}: ${ (it.second*100).toFormattedString() }%" }
                        } else {
                            // Recommendation Mode: show top-3 prominently
                            val top = prediction.topResults
                            val main = prediction.predictedCrop
                            val mainConf = prediction.confidence * 100
                            predictionResultColor = Color.Black
                            predictionResultText = buildString {
                                append("RECOMMENDED CROP:\n")
                                append("$main\nConfidence: ${mainConf.toFormattedString()}%\n\n")
                                append("Top suggestions:\n")
                                top.forEach { (label, prob) ->
                                    append("${label}: ${(prob*100).toFormattedString()}%\n")
                                }
                                if (!enableValidation) {
                                    append("\nNote: Validation is experimental and currently disabled.")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.5f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFF81C784),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 6.dp)
                ) {
                    Text(text = "Predict", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(340.dp),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = predictionResultText,
                            color = predictionResultColor,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

fun Float.toFormattedString(decimals: Int = 2): String {
    return String.format("%.${decimals}f", this)
}

@Preview(showBackground = true)
@Composable
fun SuggestionPreview() {
    MaterialTheme {
        SmartSuggestions(
            navController = rememberNavController()
        )
    }
}