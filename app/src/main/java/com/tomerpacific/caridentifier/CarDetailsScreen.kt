package com.tomerpacific.caridentifier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun CarDetailsScreen(mainViewModel: MainViewModel) {

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

        when (carDetails.value) {
            null -> Spinner()
            else -> CarInformation(carDetails.value!!)
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

    val keysId = "keysIcon"
    val currentOwnershipText = buildAnnotatedString {
        append(" בעלות נוכחית: ${details.ownership} ")
        appendInlineContent(keysId, "[icon]")
    }

    val currentOwnershipInlineContent = mapOf(
        Pair(
            keysId,
            InlineTextContent(
                Placeholder(
                    width = 20.sp,
                    height = 20.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(painterResource(id = R.drawable.ic_key_icon),"Key Icon",
                    tint = Color(254, 219, 0),
                    modifier = Modifier.fillMaxSize())
            }
        )
    )

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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(currentOwnershipText, inlineContent = currentOwnershipInlineContent, fontSize = 20.sp)
    }
}

private fun getCarManufacturer(manufacturer: String): String {
    return when (manufacturer) {
        "טויוטה" -> "Toyota"
        "סובארו" -> "Subaru"
        "סוזוקי" -> "Suzuki"
        "סיאט" -> "Seat"
        "סאאב" -> "Saab"
        "סאנגיונג" -> "Sangyong"
        "פורד" -> "Ford"
        "פיאט" -> "Fiat"
        "פולקסווגן" -> "Volkswagen"
        "פיג'ו" -> "Peugeot"
        "פונטיאק" -> "Pontiac"
        "פורשה" -> "Porsche"
        else -> "Unknown Manufacturer"
    }
}

