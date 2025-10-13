package amey.bhogle.agricare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(selectedItem: String, onItemSelected: (String) -> Unit){
    val  items = listOf("Language", "Home", "Suggest", "Help", "Settings")

    Box(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)) // Curved corners
        .background(Color(0xFF2F6138))
    )   {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 8.dp) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItem == item,
                    onClick = { onItemSelected(item) },
                    icon = {
                        when (item) {
                            "Language" -> Icon(
                                painter = painterResource(id = R.drawable.language_icon),
                                contentDescription = "Language Icon",
                                modifier = Modifier.size(24.dp)
                            )

                            "Home" -> Icon(
                                painter = painterResource(id = R.drawable.home_icon),
                                contentDescription = "Home Icon", modifier = Modifier.size(24.dp)
                            )

                            "Suggest" -> Icon(
                                painter = painterResource(id = R.drawable.suggest_icon),
                                contentDescription = "Suggest Icon", modifier = Modifier.size(24.dp)
                            )

                            "Help" -> Icon(
                                painter = painterResource(id = R.drawable.help_icon),
                                contentDescription = "Help Icon", modifier = Modifier.size(24.dp)
                            )

                            "Settings" -> Icon(
                                painter = painterResource(id = R.drawable.setting_icon),
                                contentDescription = "Settings Icon",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = { Text(text = item) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF388E3C),
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White
                    )
                )
            }
        }
    }
}