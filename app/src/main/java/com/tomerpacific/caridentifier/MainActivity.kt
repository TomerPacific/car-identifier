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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
                        Row(modifier = Modifier.fillMaxWidth(),
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
                                        buttonText = "Search By Picture",
                                        drawableId = R.drawable.license_plate,
                                        drawableContentDescription = "License Plate"
                                    )
                                    CarLicensePlateSearchOptionButton(
                                        buttonText = "Search By Typing",
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


    @Composable
    fun LicensePlateNumberDialog() {

        val focusRequester = remember {
            FocusRequester()
        }

        val licensePlateNumber = remember {
            mutableStateOf("")
        }

        AlertDialog(
            onDismissRequest = {

            },
            text = {
                Column {
                    Text("Enter License Plate Number", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequester),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        value = licensePlateNumber.value,
                        onValueChange = {
                            licensePlateNumber.value = it
                        },
                        placeholder = {
                            Text("License Plate Number")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Blue,
                            unfocusedBorderColor = Color.Black,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {

                    },
                    enabled = licensePlateNumber.value.isNotEmpty()
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {

                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
