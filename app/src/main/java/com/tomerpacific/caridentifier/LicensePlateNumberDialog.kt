package com.tomerpacific.caridentifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun LicensePlateNumberDialog(navController: NavController) {

    val focusRequester = remember {
        FocusRequester()
    }

    var licensePlateNumberState by remember {
        mutableStateOf(
            TextFieldValue(
                text = ""
            )
        )
    }

    val licensePlateInputPattern = Regex("^[0-9-]*\$")

    val validLicensePlatePattern = Regex("^[0-9]{2,3}-[0-9]{2,3}-[0-9]{2,3}")

    val mainViewModel: MainViewModel = viewModel()

    var isLicensePlateLengthLimitReached by remember {
        mutableStateOf(false)
    }

    AlertDialog(
        onDismissRequest = {
            navController.popBackStack()
        },
        text = {
            Column {
                Text("הכנס מספר לוחית רישוי בן 7 או 8 ספרות", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = Color(211,178,13,255),
                        focusedContainerColor = Color(211,178,13,255),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    value = licensePlateNumberState,
                    onValueChange = {

                        if (wasCharacterDeleted(it.text, licensePlateNumberState.text)) {
                            isLicensePlateLengthLimitReached = false
                            licensePlateNumberState = it
                            return@TextField
                        }


                        if (it.text.length > 10) {
                            isLicensePlateLengthLimitReached = true
                            return@TextField
                        }

                        if (it.text.isEmpty() || licensePlateInputPattern.matches(it.text)) {
                            licensePlateNumberState = it
                        }
                        val formattedText = when (it.text.length) {
                          2 -> "${it.text.substring(0,2)}-"
                          6 -> "${it.text.substring(0,2)}-${it.text.substring(3,6)}-"
                          in 10..11 -> "${it.text.substring(0,2)}${it.text.substring(3,4)}-${it.text.substring(4, 6)}-${it.text.substring(7, it.text.length)}"
                          else -> it.text
                        }
                        licensePlateNumberState = TextFieldValue(
                                text = formattedText,
                                selection = TextRange(formattedText.length)
                            )
                    },
                    placeholder = {
                        Text("הקלד מספר לוחית רישוי")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = isLicensePlateLengthLimitReached,
                    supportingText = {
                        if (isLicensePlateLengthLimitReached) {
                            Text(
                                "מספר לוחית רישוי יכול להכיל עד 10 תווים",
                                modifier = Modifier.fillMaxWidth(),
                                color = Color.Red)
                        }
                    },
                    trailingIcon = {
                        if (isLicensePlateLengthLimitReached) {
                            Icon(Icons.Filled.Info,"error", tint = Color.Red)
                        }
                    }
                )
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isLicensePlateValid(licensePlateNumberState.text, validLicensePlatePattern)) {
                        mainViewModel.getCarDetails(licensePlateNumberState.text)
                        navController.popBackStack()
                    }
                },
                enabled = licensePlateNumberState.text.isNotEmpty()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

private fun isLicensePlateValid(licensePlateNumber: String, pattern: Regex): Boolean {
    return  pattern.matches(licensePlateNumber) &&
            licensePlateNumber.length >= 9 &&
            licensePlateNumber.length <= 10

}

private fun wasCharacterDeleted(currentText: String, previousText: String): Boolean {
    return currentText.length < previousText.length
}