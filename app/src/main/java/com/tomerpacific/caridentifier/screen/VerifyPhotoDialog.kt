package com.tomerpacific.caridentifier.screen

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tomerpacific.caridentifier.isLicensePlateNumberValid
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.Screen
import java.io.IOException


@Composable
fun VerifyPhotoDialog(imageUri: Uri,
                      navController: NavController,
                      mainViewModel: MainViewModel) {

    val context = LocalContext.current

    Dialog(
        onDismissRequest = {
            navController.popBackStack()
        }) {
        Card(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
            shape = RoundedCornerShape(16.dp),) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Proceed with this image?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(20.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .build(),
                    contentDescription = "icon",
                    contentScale = ContentScale.Inside,
                )
                Spacer(modifier = Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                        navController.popBackStack()
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(212, 37, 11)),
                        modifier = Modifier.padding(start = 6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "No"
                        )
                    }
                    Button(
                        onClick = {
                        val recognizer =
                            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                        try {
                            val image = InputImage.fromFilePath(context, imageUri)
                            recognizer.process(image)
                                .addOnSuccessListener { visionText ->
                                    val licensePlateNumber =
                                        getLicensePlateNumberFromImageText(visionText)
                                    when (licensePlateNumber) {
                                        null -> {
                                            navController.previousBackStackEntry?.savedStateHandle?.set("error", "License plate number not found. Please take a photo which contains a license plate number.")
                                            navController.popBackStack()
                                            return@addOnSuccessListener
                                        }
                                        else -> {
                                            mainViewModel.getCarDetails(context, licensePlateNumber)
                                            navController.navigate(Screen.CarDetailsScreen.route)
                                            return@addOnSuccessListener
                                        }
                                    }
                                }
                                .addOnFailureListener { _ ->
                                    navController.previousBackStackEntry?.savedStateHandle?.set("error", "License plate number not found. Please take a photo which contains a license plate number.")
                                    navController.popBackStack()
                                }
                        } catch (e: IOException) {
                            navController.popBackStack()
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(50, 168, 82)),
                        modifier = Modifier.padding(end = 6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Yes"
                        )
                    }
                }
            }
        }
    }
}

private fun getLicensePlateNumberFromImageText(text: Text): String? {
    for (block in text.textBlocks) {
        var blockText = block.text
        if (isLicensePlateNumberValid(blockText)) {
            blockText = blockText.replace(":", "-")
            return blockText
        }
    }

    return null
}