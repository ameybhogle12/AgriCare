package amey.bhogle.agricare

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController() // create navController
                    val systemUiController = rememberSystemUiController()
                    val useDarkIcons = false // white text/icons on status bar

                    SideEffect {
                        systemUiController.setStatusBarColor(
                            color = Color(0xFF2F6138), //same green as your header
                            darkIcons = useDarkIcons
                        )
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "home" // default screen
                    ) {
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("suggestion") {
                            SmartSuggestions(navController)
                        }
                        composable("health_tips"){
                            HealthTipsScreen(navController)
                        }
                        composable("language") {
                            LanguageSelectionScreen(navController)
                        }
                        composable("settings") {
                            SettingsScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            BottomNavBar(selectedItem = stringResource(R.string.nav_home)) { selected ->
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
        Box(modifier = Modifier.fillMaxSize()
            .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            //UI Contents
            Column(modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 56.dp)
            )

            {
                // Header Box for the App Title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2F6138))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
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
                    Text(text = stringResource(R.string.news_title), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                // Call the stable NewsCarousel composable
                Spacer(modifier = Modifier.height(25.dp))

                //Images
                NewsCarousel()

                Spacer(modifier = Modifier.height(25.dp))

                //Buttons
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ElevatedButton(
                        onClick = {
                            navController.navigate("suggestion") // navigate to SuggestionScreen
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(top = 8.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFFA3DC9A)
                        )
                    ){
                        Text(text = stringResource(R.string.smart_suggestions_button),
                            color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Text(
                        text = stringResource(R.string.smart_suggestions_subtitle),
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    ElevatedButton(
                        onClick = {
                            /*Handle Farming tips screen*/
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(top = 8.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFFA3DC9A)
                        )
                    ) {
                        Text(text = stringResource(R.string.farming_tips_button),
                            color = Color.Black, fontWeight = FontWeight.Bold,fontSize = 18.sp)
                    }
                    Text(
                        text = stringResource(R.string.farming_tips_subtitle),
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )

                    ElevatedButton(
                        onClick = {
                            navController.navigate("health_tips") // navigate to HealthTips
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(top = 8.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color(0xFFA3DC9A)
                        )
                    ) {
                        Text(text = stringResource(R.string.health_tips_button),
                            color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Text(
                        text = stringResource(R.string.health_tips_subtitle),
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )


                    Text(
                        text = stringResource(R.string.sync_data),
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable{ /* Implement sync data functionality */ }
                            .padding(top = 18.dp)
                    )
                    Text(
                        text = stringResource(R.string.last_sync),
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,

                        )
                }
            }
        }
    }

}

/******************************************************/
// Carousel composable
@Composable
fun NewsCarousel() {
    // Data class to hold information for each carousel item
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val carouselItems = remember {
        listOf(
            CarouselItem(0, R.drawable.farmingimg, "Agricultural news image 1"),

            CarouselItem(1, R.drawable.doctorimg, "Agricultural news image 2"),

            CarouselItem(1, R.drawable.woman, "Agricultural news image 3"),

            CarouselItem(1, R.drawable.bull, "Agricultural news image 4")
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

/******************************************************/
//Bottom Navigation Bar


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        HomeScreen(navController = rememberNavController())
    }
}

