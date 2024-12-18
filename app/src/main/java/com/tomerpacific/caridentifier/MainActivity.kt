package com.tomerpacific.caridentifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.ui.theme.CarIdentifierTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val shouldDisplayDialogToTypeLicensePlate = mainViewModel.shouldDisplayDialogToTypeLicenseNumber.collectAsState()

            CarIdentifierTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top) {
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center) {
                            Text("Car Identifier",
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp,
                                textAlign = TextAlign.Center)
                        }
                        Spacer(modifier = Modifier.size(200.dp))
                        Box(modifier = Modifier.fillMaxSize()) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    CarLicensePlateSearchOptionButton(
                                        buttonText = "חפש לפי תמונה",
                                        drawableId = R.drawable.license_plate,
                                        drawableContentDescription = "License Plate"
                                    )
                                    CarLicensePlateSearchOptionButton(
                                        buttonText = "חפש לי מספר",
                                        drawableId = R.drawable.keyboard,
                                        drawableContentDescription = "Smartphone Keyboard"
                                    )
                                }
                            }
                        }
                    if (shouldDisplayDialogToTypeLicensePlate.value) {
                        LicensePlateNumberDialog()
                    }
                }
            }
        }
    }
}
