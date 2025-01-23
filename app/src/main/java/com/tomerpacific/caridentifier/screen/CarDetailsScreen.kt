package com.tomerpacific.caridentifier.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.composable.CarDetailWithIcon
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.getCarManufacturer
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun CarDetailsScreen(mainViewModel: MainViewModel) {

    val carDetails = mainViewModel.carDetails.collectAsState()

    val networkError = mainViewModel.networkError.collectAsState()

    val columnVerticalArrangement: Arrangement.Vertical = when (carDetails.value) {
        null -> Arrangement.Center
        else -> Arrangement.Top
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = columnVerticalArrangement
    ) {

        if (carDetails.value == null && networkError.value == null) {
            Spinner()
        } else if (carDetails.value != null) {
            CarInformation(carDetails.value!!)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = " לא ניתן להשיג את פרטי הרכב. נסו שנית.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = TextStyle(textDirection = TextDirection.Rtl)
                )
            }
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
            tint = Color(0, 0, 0),
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
    }
