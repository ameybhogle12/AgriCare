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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavBar(selectedItem: String, onItemSelected: (String) -> Unit){
    val context = LocalContext.current
    val items = listOf(
        context.getString(R.string.nav_language),
        context.getString(R.string.nav_home),
        context.getString(R.string.nav_suggest),
        context.getString(R.string.nav_help),
        context.getString(R.string.nav_settings)
    )

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
                            context.getString(R.string.nav_language) -> Icon(
                                painter = painterResource(id = R.drawable.language_icon),
                                contentDescription = stringResource(R.string.cd_language_icon),
                                modifier = Modifier.size(24.dp)
                            )

                            context.getString(R.string.nav_home) -> Icon(
                                painter = painterResource(id = R.drawable.home_icon),
                                contentDescription = stringResource(R.string.cd_home_icon),
                                modifier = Modifier.size(24.dp)
                            )

                            context.getString(R.string.nav_suggest) -> Icon(
                                painter = painterResource(id = R.drawable.suggest_icon),
                                contentDescription = stringResource(R.string.cd_suggest_icon),
                                modifier = Modifier.size(24.dp)
                            )

                            context.getString(R.string.nav_help) -> Icon(
                                painter = painterResource(id = R.drawable.help_icon),
                                contentDescription = stringResource(R.string.cd_help_icon),
                                modifier = Modifier.size(24.dp)
                            )

                            context.getString(R.string.nav_settings) -> Icon(
                                painter = painterResource(id = R.drawable.setting_icon),
                                contentDescription = stringResource(R.string.cd_settings_icon),
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