package com.tomerpacific.caridentifier.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.res.stringResource
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
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.isLicensePlateNumberValid
import com.tomerpacific.caridentifier.model.MainViewModel
import com.tomerpacific.caridentifier.model.Screen
import java.io.IOException
import androidx.core.net.toUri


val textRecognizer =
    TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

@Composable
fun VerifyPhotoDialog(imageUri: String,
                      navController: NavController,
                      mainViewModel: MainViewModel) {

    val context = LocalContext.current
    val uri: Uri?

    try {
        uri = imageUri.toUri()
    } catch (e: Exception) {
        Log.e("VerifyPhotoDialog", "Invalid URI: $imageUri", e)
        mainViewModel.triggerSnackBarEvent(stringResource(R.string.invalid_image_uri_error))
        navController.popBackStack()
        return
    }

    Dialog(
        onDismissRequest = {
            navController.popBackStack()
        }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.verify_photo_msg), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.size(20.dp))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uri)
                        .build(),
                    contentDescription = "icon",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier.heightIn(max = 300.dp).fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                        navController.popBackStack()
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.padding(start = 6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "No"
                        )
                    }
                    Button(
                        onClick = { processImage(context, uri, mainViewModel, navController) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
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

private fun processImage(context: Context,
                         imageUri: Uri,
                         mainViewModel: MainViewModel,
                         navController: NavController) {

    try {
        val image = InputImage.fromFilePath(context, imageUri)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val licensePlateNumber =
                    getLicensePlateNumberFromImageText(visionText)
                when (licensePlateNumber) {
                    null -> {
                        mainViewModel.triggerSnackBarEvent(context.getString(R.string.no_license_plate_error))
                        navController.popBackStack()
                        return@addOnSuccessListener
                    }
                    else -> {
                        mainViewModel.getCarDetails(licensePlateNumber)
                        navController.navigate(Screen.CarDetailsScreen.route)
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnFailureListener { _ ->
                mainViewModel.triggerSnackBarEvent(context.getString(R.string.no_license_plate_error))
                navController.popBackStack()
            }
    } catch (e: IOException) {
        mainViewModel.triggerSnackBarEvent(e.message ?: context.getString(R.string.error_processing_image))
        navController.popBackStack()
    }
}

private fun getLicensePlateNumberFromImageText(text: Text): String? {
    for (block in text.textBlocks) {
        val blockText = block.text
        if (isLicensePlateNumberValid(blockText)) {
            return blockText.replace(":", "-")
        }
    }

    return null
}
