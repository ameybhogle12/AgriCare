package amey.bhogle.agricare

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFFACC))) {
        // Header Box for the App Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4CAF50))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AgriCare",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Box for the "News!" title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF59D))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "News!", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
        // Call the stable NewsCarousel composable
        NewsCarousel()
        // You can add more content below the carousel if needed
    }
}

@Composable
fun NewsCarousel() {
    // Data class to hold information for each carousel item
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    // A list of items for the carousel.

    // REMEMBER: You must replace these R.drawable names with your actual image files.

    val carouselItems = remember {
        listOf(
            CarouselItem(0, R.drawable.farmingimg, "Agricultural news image 1"),
            // Example using your existing image
            // Add more items here with your other images
            CarouselItem(1, R.drawable.doctorimg, "Agricultural news image 2"),
            // CarouselItem(2, R.drawable.another_image, "Agricultural news image 3"),
        )
    }
    // State for the pager, which tracks the current page
    val pagerState = rememberPagerState(pageCount = {
        carouselItems.size
    })
    // The stable HorizontalPager composable
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 32.dp), // Shows a preview of the next/previous items
        pageSpacing = 8.dp // Spacing between the items

    ) { page ->
        val item = carouselItems[page]
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(MaterialTheme.shapes.medium), // Apply rounded corners
            painter = painterResource(id = item.imageResId),
            contentDescription = item.contentDescription,
            contentScale = ContentScale.Crop
        )

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        HomeScreen()
    }
}

