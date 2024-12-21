package com.tomerpacific.caridentifier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun CarDetailsScreen(mainViewModel: MainViewModel) {

    val carDetails = mainViewModel.carDetails.collectAsState()

    val arrangement: Arrangement.Vertical = when (carDetails.value) {
        null -> Arrangement.Center
        else -> Arrangement.Top
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = arrangement
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
        Text(" בעלות נוכחית: ${details.ownership}", fontSize = 20.sp)
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

