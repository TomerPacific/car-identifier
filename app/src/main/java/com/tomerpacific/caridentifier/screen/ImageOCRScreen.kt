package com.tomerpacific.caridentifier.screen

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageOCRScreen(imageUri: Uri) {

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .build(),
            contentDescription = "icon",
            contentScale = ContentScale.Inside,
            modifier = Modifier.size(300.dp).border(
                BorderStroke(3.dp, Color.Black))
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(text = "Proceed with this image?", fontSize = 20.sp)
        Spacer(modifier = Modifier.size(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = {}, modifier = Modifier.background(Color.Green)) {
                Text(text = "Yes")
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Yes"
                )
            }
            TextButton(onClick = {}, modifier = Modifier.background(Color.Red)) {
                Text(text = "No")
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "No"
                )
            }

        }
    }


}