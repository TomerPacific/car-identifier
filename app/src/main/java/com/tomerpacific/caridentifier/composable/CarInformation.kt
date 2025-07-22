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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun Details(mainViewModel: MainViewModel) {

    val context = LocalContext.current

    val uiState by mainViewModel.mainUiState.collectAsState()

    val columnVerticalArrangement: Arrangement.Vertical = when (uiState.carDetails) {
        null -> Arrangement.Center
        else -> Arrangement.Top
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = columnVerticalArrangement
    ) {
        when {
            uiState.isLoading -> {
                LoaderAnimation(R.raw.license_plate_scan_animation)
            }
            uiState.carDetails != null -> {
                CarInformation(uiState.carDetails!!)
            }
            uiState.errorMessage != null -> {
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
                Text(text = stringResource(R.string.car_details_not_obtained_error_msg),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = TextStyle(textDirection = TextDirection.Rtl)
                )
                Text(text = uiState.errorMessage!!,
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (mainViewModel.shouldShowRetryRequestButton()) {
                    IconButton(onClick = {
                        mainViewModel.getCarDetails()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                    }
                }
            }
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

    CarDetailWithIcon(iconId = "checkIcon",
        labelText = stringResource(R.string.last_test_date_for_car),
        content = details.lastTestDate,
        tooltipDescription = stringResource(R.string.last_test_date_tooltip_explanation)
    ) {
        Icon(
            Icons.Default.Check,
            contentDescription = "Check Icon",
            tint = Color(0, 0, 0)
        )
    }

    CarDetailWithIcon(
        iconId = "keysIcon",
        labelText = stringResource(R.string.current_ownership),
        content = details.ownership,
        tooltipDescription = stringResource(R.string.current_ownership_tooltip_explanation)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_key_icon),"Key Icon",
            tint = Color(254, 219, 0))
    }

    CarDetailWithIcon(
        iconId = "fuelIcon",
        labelText = stringResource(R.string.fuel_type),
        content = details.fuelType,
        tooltipDescription = stringResource(R.string.fuel_type_tooltip_explanation)

    ) {
        Icon(
            painterResource(id = R.drawable.ic_fuel_type), "Fuel Icon",
            tint = Color(18, 80, 255)
        )
    }

    CarDetailWithIcon(
        iconId = "safetyIcon",
        labelText = stringResource(R.string.safety_rating),
        content = "${details.safetyFeatureLevel}/8",
        tooltipDescription = stringResource(R.string.safety_rating_tooltip_explanation)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_shield), "Safety Icon",
            tint = Color(50, 168, 82)
        )
    }

    CarDetailWithIcon(
        iconId = "pollutionIcon",
        labelText = stringResource(R.string.air_pollution_rating),
        content = "${details.pollutionLevel}/15",
        tooltipDescription = stringResource(R.string.air_pollution_rating_tooltip_explanation)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_car_alert), "Pollution Icon",
            tint = Color(255, 0, 0)
        )
    }

    CarDetailWithIcon(
        iconId = "paletteIcon",
        labelText = stringResource(R.string.car_color),
        content = details.color,
        tooltipDescription = stringResource(R.string.car_color_tooltip_explanation)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_palette), "Palette Icon",
            tint = Color(209, 150, 105)
        )
    }

    CarDetailWithIcon(
        iconId = "roadIcon",
        labelText = stringResource(R.string.first_time_on_road),
        content = details.firstOnRoadDate,
        tooltipDescription = stringResource(R.string.first_time_on_road_tooltip_explanation)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_road), "Road Icon",
            tint = Color(0, 0, 0)
        )
    }
}