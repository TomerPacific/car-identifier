package com.tomerpacific.caridentifier.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.concatenateCarMakeAndModel
import com.tomerpacific.caridentifier.model.CarDetails
import com.tomerpacific.caridentifier.model.MainViewModel

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
            LoaderAnimation(R.raw.license_plate_scan_animation)
        } else if (carDetails.value != null) {
            CarInformation(carDetails.value!!)
        } else if (serverError.value != null) {
            Image(
                modifier = Modifier
                    .size(200.dp)
                    .border(
                        BorderStroke(1.dp, Color.Black),
                        CircleShape
                    )
                    .clip(CircleShape),
                painter = painterResource(R.drawable.broken_car),
                contentDescription = "broken car",
            )
            Spacer(modifier = Modifier.size(100.dp))
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
fun CarInformation(details: CarDetails) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            concatenateCarMakeAndModel(details),
            fontSize = 25.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(50.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(250.dp)
                .border(
                    BorderStroke(1.dp, Color.Black),
                    CircleShape
                )
                .clip(CircleShape),
            painter = painterResource(R.drawable.car_display),
            contentDescription = "mechanic in garage",
        )
    }
    Spacer(modifier = Modifier.height(50.dp))
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
        Icon(
            painterResource(id = R.drawable.ic_key_icon),"Key Icon",
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