package com.tomerpacific.caridentifier.screen

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageOCRScreen(imageUri: Uri) {

    Text(text = "Image OCR Screen")
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
        .build(),
    contentDescription = "icon",
    contentScale = ContentScale.Inside,
    modifier = Modifier.size(30.dp)
    )

}