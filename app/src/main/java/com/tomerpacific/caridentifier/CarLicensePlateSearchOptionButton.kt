package com.tomerpacific.caridentifier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.model.Screen


@Composable
fun CarLicensePlateSearchOptionButton(buttonText: String,
                                      drawableId: Int,
                                      drawableContentDescription: String,
                                      navController: NavController,
                                      shouldDisableButton:Boolean = false) {

    Column(
        modifier = Modifier.then(
            if (shouldDisableButton) {
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
            } else {
                Modifier.clickable {
                    val navigationRoute = when (drawableId) {
                        R.drawable.license_plate -> Screen.CameraPermission.route
                        else -> Screen.LicensePlateNumberInput.route
                    }
                    navController.navigate(route = navigationRoute)
                }
            }),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(150.dp)
                .border(
                    BorderStroke(1.dp, Color.Black),
                    CircleShape
                )
                .clip(CircleShape),
            painter = painterResource(drawableId),
            contentDescription = drawableContentDescription,
            contentScale = ContentScale.Crop,
            colorFilter = decideOnColorFilter(drawableId, shouldDisableButton)
        )
        Text(buttonText)
    }
}

private fun decideOnColorFilter(drawableId: Int, shouldDisableButton: Boolean): ColorFilter? {
    if (drawableId == R.drawable.license_plate && shouldDisableButton) {
        return ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    }
    return null
}