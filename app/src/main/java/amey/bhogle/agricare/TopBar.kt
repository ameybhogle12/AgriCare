package amey.bhogle.agricare

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(navController: NavController, title: String) {
    val currentBackStack = navController.currentBackStackEntry
    val currentDestination = currentBackStack?.destination?.route

    androidx.compose.material3.TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            // Show back arrow only if not on Home
            if (currentDestination != "home") {
                androidx.compose.material3.IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    androidx.compose.material3.Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF4CAF50),
            titleContentColor = Color.White
        )
    )
}
