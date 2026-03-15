package com.tomerpacific.caridentifier.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.getLicensePlateNumberFromImageText
import com.tomerpacific.caridentifier.model.CarViewModel
import com.tomerpacific.caridentifier.model.Screen

@Composable
fun VerifyPhotoDialog(
    imageUri: String,
    navController: NavController,
    carViewModel: CarViewModel,
) {
    val context = LocalContext.current
    val textRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    DisposableEffect(Unit) {
        onDispose {
            textRecognizer.close()
        }
    }

    val uri: Uri = try {
        imageUri.toUri()
    } catch (e: Exception) {
        Log.e("VerifyPhotoDialog", "Invalid URI: $imageUri", e)
        carViewModel.triggerSnackBarEvent(stringResource(R.string.invalid_image_uri_error))
        navController.popBackStack()
        return
    }

    Dialog(onDismissRequest = { navController.popBackStack() }) {
        VerifyPhotoCard(
            uri = uri,
            onCancel = { navController.popBackStack() },
            onConfirm = {
                processImage(context, uri, carViewModel, navController, textRecognizer)
            }
        )
    }
}

@Composable
private fun VerifyPhotoCard(
    uri: Uri,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.verify_photo_msg),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.size(20.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .build(),
                contentDescription = "captured image",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .heightIn(max = 300.dp)
                    .fillMaxWidth(),
            )
            Spacer(modifier = Modifier.size(20.dp))
            VerifyPhotoButtons(onCancel = onCancel, onConfirm = onConfirm)
        }
    }
}

@Composable
private fun VerifyPhotoButtons(
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.padding(start = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "No",
            )
        }
        Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
            modifier = Modifier.padding(end = 16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Yes",
            )
        }
    }
}

private fun processImage(
    context: Context,
    imageUri: Uri,
    carViewModel: CarViewModel,
    navController: NavController,
    textRecognizer: TextRecognizer,
) {
    var bitmap: Bitmap? = null
    var grayscaleBitmap: Bitmap? = null
    try {
        bitmap = getBitmapFromUri(context, imageUri)
        grayscaleBitmap = toGrayscale(bitmap)
        val image = InputImage.fromBitmap(grayscaleBitmap, 0)

        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val licensePlateNumber = getLicensePlateNumberFromImageText(visionText)
                if (licensePlateNumber == null) {
                    carViewModel.triggerSnackBarEvent(context.getString(R.string.no_license_plate_error))
                    navController.popBackStack()
                } else {
                    carViewModel.getCarDetails(licensePlateNumber)
                    navController.navigate(Screen.CarDetailsScreen.route)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("VerifyPhotoDialog", "Text recognition failed", exception)
                carViewModel.triggerSnackBarEvent(context.getString(R.string.no_license_plate_error))
                navController.popBackStack()
            }.addOnCompleteListener {
                grayscaleBitmap?.recycle()
                bitmap?.recycle()
            }
    } catch (e: Exception) {
        grayscaleBitmap?.recycle()
        bitmap?.recycle()
        carViewModel.triggerSnackBarEvent(e.message ?: context.getString(R.string.error_processing_image))
        navController.popBackStack()
    }
}

private fun getBitmapFromUri(
    context: Context,
    imageUri: Uri,
): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        getBitmapWithImageDecoder(context, imageUri)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    }
}

@RequiresApi(Build.VERSION_CODES.P)
private fun getBitmapWithImageDecoder(
    context: Context,
    imageUri: Uri,
): Bitmap {
    val source = ImageDecoder.createSource(context.contentResolver, imageUri)
    return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
        decoder.isMutableRequired = false
    }
}

private fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
    val bmpToProcess =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            bmpOriginal.config == Bitmap.Config.HARDWARE
        ) {
            bmpOriginal.copy(Bitmap.Config.ARGB_8888, false)
        } else {
            bmpOriginal
        }

    val bmpGrayscale = createBitmap(bmpToProcess.width, bmpToProcess.height)
    val c = Canvas(bmpGrayscale)
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    val f = ColorMatrixColorFilter(cm)
    paint.colorFilter = f
    c.drawBitmap(bmpToProcess, 0f, 0f, paint)

    if (bmpToProcess !== bmpOriginal) {
        bmpToProcess.recycle()
    }

    return bmpGrayscale
}
