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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LicensePlateNumberDialog() {

    val focusRequester = remember {
        FocusRequester()
    }

    val licensePlateNumber = remember {
        mutableStateOf("")
    }

    val pattern = remember { Regex("^\\d+\$") }

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
                        if (it.isEmpty() || pattern.matches(it)) {
                            licensePlateNumber.value = it
                        }
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