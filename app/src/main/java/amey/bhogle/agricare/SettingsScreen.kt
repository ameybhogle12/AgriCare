package amey.bhogle.agricare

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = stringResource(R.string.nav_settings)
            )
        },
        bottomBar = {
            BottomNavBar(selectedItem = stringResource(R.string.nav_settings)) { selected ->
                when (selected) {
                    context.getString(R.string.nav_home) -> navController.navigate("home")
                    context.getString(R.string.nav_suggest) -> navController.navigate("suggestion")
                    context.getString(R.string.nav_language) -> navController.navigate("language")
                    context.getString(R.string.nav_help) -> navController.navigate("health_tips")
                    context.getString(R.string.nav_settings) -> navController.navigate("settings")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = stringResource(R.string.cd_background_image),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .zIndex(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.nav_settings),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.settings_coming_soon),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}

