package amey.bhogle.agricare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur // Import for the blur modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

//@Preview(showBackground = true)
@Composable
fun HealthTipsScreen(navController: NavController) {
    val context = LocalContext.current

    val tips = listOf(
        HealthTip(
            context.getString(R.string.health_fever),
            context.getString(R.string.health_fever_tip),
            R.drawable.fever
        ),
        HealthTip(
            context.getString(R.string.health_diarrhea),
            context.getString(R.string.health_diarrhea_tip),
            R.drawable.ors
        ),
        HealthTip(
            context.getString(R.string.health_cough),
            context.getString(R.string.health_cough_tip),
            R.drawable.turmericmilk
        ),
        HealthTip(
            context.getString(R.string.health_cuts),
            context.getString(R.string.health_cuts_tip),
            R.drawable.dettol
        )
    )

    Scaffold(
        topBar = { AppTopBar(navController, title = stringResource(R.string.health_tips_title)) },
        bottomBar = {
            BottomNavBar(selectedItem = stringResource(R.string.nav_help)) { selected ->
                when (selected) {
                    context.getString(R.string.nav_home) -> navController.navigate("home")
                    context.getString(R.string.nav_suggest) -> navController.navigate("suggestion")
                    context.getString(R.string.nav_language) -> navController.navigate("language")
                    context.getString(R.string.nav_help) -> navController.navigate("health_tips")
                    context.getString(R.string.nav_settings) -> navController.navigate("settings")
                }
            }
        }
    ) {
        innerPadding ->
        Box(modifier = Modifier.fillMaxSize()
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
                    .zIndex(1f)
            ) {

                // List of tips
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)

                ) {
                    items(tips.size) { index ->
                        HealthTipCard(tip = tips[index])
                    }
                }
            }
    }
    }


}

@Composable
fun HealthTipCard(tip: HealthTip) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expanded = !expanded }, // toggle expand on click
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = tip.imageRes),
                    contentDescription = tip.title,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tip.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tip.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        maxLines = if (expanded) Int.MAX_VALUE else 1 // show 1 line before expand
                    )
                }
            }

            // Expanded section with smooth animation
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = tip.imageRes),
                        contentDescription = tip.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tip.description,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthTipsPreview() {
    MaterialTheme {
        HealthTipsScreen(
            navController = rememberNavController()
        )
    }
}