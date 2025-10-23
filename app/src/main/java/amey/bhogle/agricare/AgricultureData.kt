package amey.bhogle.agricare

// AgricultureData.kt

// Defines the 10 input features required by Model V3
data class TenFeatures(
    // 6 Soil/Nutrient Features
    val N: Float, val P: Float, val K: Float,
    val Ph: Float, val S: Float, val Zn: Float,

    // 4 Weather/Climate Features
    val PRECTOTCORR_Su: Float,  // Monsoon Rain
    val T2M_MAX_Sp: Float,      // Spring Max Temp
    val T2M_MIN_W: Float,       // Winter Min Temp
    val QV2M_Su: Float          // Monsoon Humidity
)

// --- Soil Type Mapping: Sets 6 Nutrient Features ---
// Maps user's soil choice to typical properties (baseline weather is neutral)
val soilTypeMap: Map<String, TenFeatures> = mapOf(
    "Alluvial Soil" to TenFeatures(N=95f, P=50f, K=160f, Ph=7.2f, S=10f, Zn=1.5f,
        PRECTOTCORR_Su=100f, T2M_MAX_Sp=30f, T2M_MIN_W=10f, QV2M_Su=14f),

    "Black (Regur) Soil" to TenFeatures(N=60f, P=45f, K=220f, Ph=8.1f, S=7f, Zn=1.0f,
        PRECTOTCORR_Su=100f, T2M_MAX_Sp=30f, T2M_MIN_W=10f, QV2M_Su=14f),

    "Red Soil" to TenFeatures(N=40f, P=30f, K=120f, Ph=6.0f, S=12f, Zn=2.0f,
        PRECTOTCORR_Su=100f, T2M_MAX_Sp=30f, T2M_MIN_W=10f, QV2M_Su=14f),

    "Laterite Soil" to TenFeatures(N=20f, P=15f, K=100f, Ph=5.0f, S=8f, Zn=1.8f,
        PRECTOTCORR_Su=100f, T2M_MAX_Sp=30f, T2M_MIN_W=10f, QV2M_Su=14f),

    "Desert Soil" to TenFeatures(N=30f, P=20f, K=80f, Ph=8.5f, S=5f, Zn=0.8f,
        PRECTOTCORR_Su=100f, T2M_MAX_Sp=30f, T2M_MIN_W=10f, QV2M_Su=14f)
)

// --- Region Mapping: Overrides 4 Weather Features ---
// Provides region-specific climate baselines.
val regionWeatherMap: Map<String, Map<String, Float>> = mapOf(
    "Himalayan Region (Cold)" to mapOf(
        "PRECTOTCORR_Su" to 150f, "T2M_MAX_Sp" to 20f,
        "T2M_MIN_W" to -2f, "QV2M_Su" to 12f
    ),
    "Northern Plains (Hot/Cold)" to mapOf(
        "PRECTOTCORR_Su" to 90f, "T2M_MAX_Sp" to 38f,
        "T2M_MIN_W" to 5f, "QV2M_Su" to 14f
    ),
    "Deccan Plateau (Semi-Arid)" to mapOf(
        "PRECTOTCORR_Su" to 80f, "T2M_MAX_Sp" to 35f,
        "T2M_MIN_W" to 10f, "QV2M_Su" to 15f
    ),
    "Coastal South (Humid)" to mapOf(
        "PRECTOTCORR_Su" to 200f, "T2M_MAX_Sp" to 30f,
        "T2M_MIN_W" to 18f, "QV2M_Su" to 18f
    ),
    "Arid Zone (Rajasthan)" to mapOf(
        "PRECTOTCORR_Su" to 40f, "T2M_MAX_Sp" to 40f,
        "T2M_MIN_W" to 8f, "QV2M_Su" to 10f
    )
)

// --- Season Refinement Logic: Fine-tunes the weather features ---
// This is used to adjust the baseline based on the selected season (Monsoon, Winter, etc.)
fun applySeasonRefinement(features: MutableMap<String, Float>, season: String) {
    when (season) {
        "Kharif (Monsoon)" -> {
            // Boost rainfall and humidity for Monsoon period
            features["PRECTOTCORR_Su"] = features["PRECTOTCORR_Su"]!! * 1.3f // +30% Rain
            features["QV2M_Su"] = features["QV2M_Su"]!! * 1.15f             // +15% Humidity
        }
        "Rabi (Winter)" -> {
            // Decrease winter minimum temperature
            features["T2M_MIN_W"] = features["T2M_MIN_W"]!! - 3.0f // Colder
            features["QV2M_Su"] = features["QV2M_Su"]!! * 0.9f     // Drier
        }
        "Zaid (Summer)" -> {
            // Boost spring/summer maximum temperature
            features["T2M_MAX_Sp"] = features["T2M_MAX_Sp"]!! + 5.0f // Hotter
            features["PRECTOTCORR_Su"] = features["PRECTOTCORR_Su"]!! * 0.5f // Less Rain
        }
    }
}