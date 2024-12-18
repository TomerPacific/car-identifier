package com.tomerpacific.caridentifier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.tomerpacific.caridentifier.model.MainViewModel

@Composable
fun LicensePlateNumberDialog() {

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

    AlertDialog(
        onDismissRequest = {
            mainViewModel.dismissLicensePlateInputDialog()
        },
        text = {
            Column {
                Text("Enter a 7 or 8 digit license plate number", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    modifier = Modifier.focusRequester(focusRequester),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    value = licensePlateNumberState,
                    onValueChange = {
                        // Handle deletion
                        if (it.text.length < licensePlateNumberState.text.length) {
                          licensePlateNumberState = it
                          return@OutlinedTextField
                        }

                        if (it.text.length > 10) {
                            return@OutlinedTextField
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Black,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    mainViewModel.dismissLicensePlateInputDialog()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}

fun isLicensePlateValid(licensePlateNumber: String, pattern: Regex): Boolean {
    return  pattern.matches(licensePlateNumber) &&
            licensePlateNumber.length >= 9 &&
            licensePlateNumber.length <= 10

}