package com.tomerpacific.caridentifier

import android.content.pm.PackageManager.PERMISSION_DENIED
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.ui.theme.CarIdentifierTheme

@Composable
fun MainScreen(navController: NavController,
               mainViewModel: MainViewModel,
               activity: MainActivity) {

    val cameraPermissionStatus = remember {
        ContextCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.CAMERA
        )
    }

    val shouldShowRationale = remember {
        shouldShowRequestPermissionRationale(activity, android.Manifest.permission.CAMERA)
    }

    val didRequestPermission = mainViewModel.didRequestCameraPermission.collectAsState()

    val shouldDisableButton = didRequestPermission.value &&
            cameraPermissionStatus == PERMISSION_DENIED &&
            !shouldShowRationale

    CarIdentifierTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Text("בחר/י באפשרות לחפש פרטים בנוגע לרכב",
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center)
                }
                Spacer(modifier = Modifier.size(150.dp))
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CarLicensePlateSearchOptionButton(
                            buttonText = "חפש לפי תמונה",
                            drawableId = R.drawable.license_plate,
                            drawableContentDescription = "License Plate",
                            navController,
                            shouldDisableButton
                        )
                        CarLicensePlateSearchOptionButton(
                            buttonText = "חפש לפי מספר",
                            drawableId = R.drawable.keyboard,
                            drawableContentDescription = "Smartphone Keyboard",
                            navController
                        )
                    }
                }
            }
        }
    }
}