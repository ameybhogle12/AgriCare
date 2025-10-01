package amey.bhogle.agricare

import androidx.annotation.DrawableRes

data class HealthTip(
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int
)
