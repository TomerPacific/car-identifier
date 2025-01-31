package com.tomerpacific.caridentifier.screen


import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.tomerpacific.caridentifier.composable.CarDetailWithIcon
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.getCarManufacturer
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun CarDetailsScreen(mainViewModel: MainViewModel) {


    var tabIndex by remember { mutableStateOf(0) }

    val tabs = listOf("Details", "Reviews", "AI")

    val serverError = mainViewModel.serverError.collectAsState()

    val searchTerm: String = mainViewModel.searchTerm

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    icon = {
                        when (index) {
                            0 -> Icon(painterResource(id = R.drawable.ic_fact_check), contentDescription = "list")
                            1 -> Icon(painterResource(id = R.drawable.ic_reviews), contentDescription = "reviews")
                            2 -> Icon(painterResource(
                                id = R.drawable.ic_chatgpt),
                                contentDescription = "chatgpt",
                                modifier = Modifier.size(40.dp))
                        }
                    }
                )
            }
        }
        when (tabIndex) {
            0 -> Details(mainViewModel, serverError)
            1 -> Reviews(searchTerm, serverError)
            2 -> {
                if (serverError.value == null) {
                    mainViewModel.getCarReview(searchTerm)
                }
                Recommendation(mainViewModel, serverError)
            }
        }
    }

}

@Composable
fun Details(mainViewModel: MainViewModel, serverError: State<String?>) {
    val carDetails = mainViewModel.carDetails.collectAsState()

    val columnVerticalArrangement: Arrangement.Vertical = when (carDetails.value) {
        null -> Arrangement.Center
        else -> Arrangement.Top
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = columnVerticalArrangement
    ) {

        if (carDetails.value == null && serverError.value == null) {
            Spinner()
        } else if (carDetails.value != null) {
            CarInformation(carDetails.value!!)
        } else if (serverError.value != null) {
            Text(text = " לא ניתן להשיג את פרטי הרכב. נסו שנית.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(textDirection = TextDirection.Rtl)
            )
            Text(text = serverError.value!!,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun Spinner() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun CarInformation(details: CarDetails) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("פרטי הרכב", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("${getCarManufacturer(details.manufacturerName)} ${details.commercialName.lowercase().replaceFirstChar { it.titlecase() }} ${details.trimLevel.lowercase().replaceFirstChar { it.titlecase() }} ${details.yearOfProduction}",
                fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(100.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(" טסט אחרון בוצע בתאריך: ${details.lastTestDate}", fontSize = 20.sp)
        }

        CarDetailWithIcon(
            iconId = "keysIcon",
            text = " בעלות נוכחית: ${details.ownership} "
        ) {
            Icon(painterResource(id = R.drawable.ic_key_icon),"Key Icon",
                tint = Color(254, 219, 0),
                modifier = Modifier.fillMaxSize())
        }

        CarDetailWithIcon(
            iconId = "fuelIcon",
            text = " סוג דלק: ${details.fuelType} "
        ) {
            Icon(
                painterResource(id = R.drawable.ic_fuel_type), "Fuel Icon",
                tint = Color(18, 80, 255),
                modifier = Modifier.fillMaxSize()
            )
        }

        CarDetailWithIcon(
            iconId = "safetyIcon",
            text = " רמת אבזור בטיחות: ${details.safetyFeatureLevel} "
        ) {
            Icon(
                painterResource(id = R.drawable.ic_shield), "Safety Icon",
                tint = Color(0, 255, 0),
                modifier = Modifier.fillMaxSize()
            )
        }

        CarDetailWithIcon(
            iconId = "pollutionIcon",
            text = " רמת זיהום אוויר: ${details.pollutionLevel} "
        ) {
            Icon(
                painterResource(id = R.drawable.ic_car_alert), "Pollution Icon",
                tint = Color(255, 0, 0),
                modifier = Modifier.fillMaxSize()
            )
        }

        CarDetailWithIcon(
            iconId = "paletteIcon",
            text = " צבע: ${details.color} "
        ) {
            Icon(
                painterResource(id = R.drawable.ic_palette), "Palette Icon",
                tint = Color(209, 150, 105),
                modifier = Modifier.fillMaxSize()
            )
        }

        CarDetailWithIcon(
            iconId = "roadIcon",
            text = " עלה לכביש: ${details.firstOnRoadDate} "
        ) {
            Icon(
                painterResource(id = R.drawable.ic_road), "Road Icon",
                tint = Color(0, 0, 0),
                modifier = Modifier.fillMaxSize()
            )
        }
}

@Composable
fun Reviews(searchTerm: String, serverError: State<String?>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (serverError.value != null) {
            Text(
                text = " לא ניתן להשיג את פרטי הרכב. נסו שנית.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(textDirection = TextDirection.Rtl)
            )
            Text(
                text = serverError.value!!,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            AndroidView(factory = {
                WebView(it).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                }
            }, update = {
                it.loadUrl("https://www.youtube.com/results?search_query=$searchTerm Review")
            })
        }
    }
}

@Composable
fun Recommendation(mainViewModel: MainViewModel, serverError: State<String?>) {

    val carReview = mainViewModel.searchTermCompletionText.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (carReview.value == null && serverError.value == null) {
            Spinner()
        } else if (carReview.value != null) {

            val prosText: String = createBulletPoints(carReview.value!!.prosList)
            val consText: String = createBulletPoints(carReview.value!!.consList)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("המלצות בינה מלאכותית", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(100.dp))
            Text(
                "Pros:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 5.dp)
            )
            Text(
                text = prosText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp)
            )
            Text(
                "Cons:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 5.dp)
            )
            Text(
                text = consText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 10.dp)
            )
        } else if (serverError.value != null) {
            Text(
                text = "יש בעיה עם הבאת התוכן המבוקש.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                style = TextStyle(textDirection = TextDirection.Rtl)
            )
            Text(
                text = serverError.value!!,
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun createBulletPoints(list: List<String>): String {
    val paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 12.sp))
    return buildAnnotatedString {
        list.forEach {
            withStyle(style = paragraphStyle) {
                append(it)
                append("\n")
            }
        }
    }.text
}
