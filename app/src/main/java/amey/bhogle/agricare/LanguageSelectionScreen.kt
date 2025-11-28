package amey.bhogle.agricare

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import java.util.*

data class Language(
    val code: String,
    val nameResId: Int,
    val nativeName: String
)

@Composable
fun LanguageSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    
    // Get current locale dynamically
    val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales.get(0)?.language ?: "en"
    } else {
        AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "en"
    }

    var selectedLanguage by remember { mutableStateOf(currentLocale) }

    val languages = listOf(
        Language("en", R.string.language_english, "English"),
        Language("hi", R.string.language_hindi, "हिन्दी"),
        Language("mr", R.string.language_marathi, "मराठी"),
        Language("gu", R.string.language_gujarati, "ગુજરાતી"),
        Language("pa", R.string.language_punjabi, "ਪੰਜਾਬੀ")
    )

    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = stringResource(R.string.language_selection_title)
            )
        },
        bottomBar = {
            BottomNavBar(selectedItem = stringResource(R.string.nav_language)) { selected ->
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
                    .zIndex(1f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.language_subtitle),
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(languages.size) { index ->
                        val language = languages[index]
                        LanguageCard(
                            language = language,
                            isSelected = selectedLanguage == language.code,
                            onClick = {
                                if (selectedLanguage != language.code) {
                                    selectedLanguage = language.code
                                    setAppLocale(context, language.code)
                                    // Recreate activity for smooth language change
                                    (context as? MainActivity)?.recreate()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageCard(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF4CAF50) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = language.nativeName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else Color.Black
            )

            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.language_icon),
                    contentDescription = stringResource(R.string.cd_language_icon),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun setAppLocale(context: Context, languageCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList(Locale.forLanguageTag(languageCode))
    } else {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(languageCode)
        )
    }
}

