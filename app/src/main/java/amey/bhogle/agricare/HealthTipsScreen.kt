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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class HealthTipsActivity  : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthTipsScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HealthTipsScreen() {
    val tips = listOf(
        HealthTip("Fever", "Stay hydrated. Wet cloth on forehead.", R.drawable.fever),
        HealthTip("Diarrhea", "ORS mix, visit health worker.", R.drawable.ors),
        HealthTip("Cough", "Turmeric milk + warm fluids.", R.drawable.turmericmilk),
        HealthTip("Cuts", "Wash with clean water, apply Dettol.", R.drawable.dettol)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            // I've temporarily added a background image here for demonstration
            // Replace R.drawable.your_background_image with your actual image resource later
            // Or remove this line if you want to test with a plain color background initially
            .background(Color(0xFFFFFACC)) // Keep this if you want a fallback color
        // For a better visual, you can use an Image Composable as the very first item
        // in the Column if you want it to truly be a background image.
        // For now, let's just make the existing background show through the blur.
    ) {
        // Header
        Text(
            text = "Health Tips",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .padding(16.dp),
        )

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

        // Bottom Navigation
        BottomNavBar()
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



@Composable
fun BottomNavBar() {
    NavigationBar(
        containerColor = Color(0xFF2E7D32),
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(selected = true, onClick = { }, icon = { }, label = { Text("Language") })
        NavigationBarItem(selected = false, onClick = { }, icon = { }, label = { Text("Home") })
        NavigationBarItem(selected = false, onClick = { }, icon = { }, label = { Text("Suggest") })
        NavigationBarItem(selected = false, onClick = { }, icon = { }, label = { Text("Help") })
        NavigationBarItem(selected = false, onClick = { }, icon = { }, label = { Text("Settings") })
    }
}