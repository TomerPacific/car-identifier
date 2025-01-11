package com.tomerpacific.caridentifier.composable

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.CameraFileUtils.takePicture
import com.tomerpacific.caridentifier.model.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun CameraPreview(navController: NavController) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val coroutineScope = rememberCoroutineScope()

    val cameraController: LifecycleCameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = { ctx: Context -> PreviewView(ctx).apply {
            scaleType = PreviewView.ScaleType.FILL_START
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            controller = cameraController
            }
        },
            onRelease = {
                cameraController.unbind()
            }
        )
        Button(onClick = {
            takePicture(cameraController, context, executor, { uri ->
                coroutineScope.launch(Dispatchers.Main){
                    navController.navigate(Screen.VerifyPhoto.route +"/${Uri.encode(uri.toString())}")
                }
            }, { imageCaptureException ->
                Log.e("CameraPreview", "Error capturing image", imageCaptureException)
                navController.popBackStack()
            })
            }) {
            Text(text = "Capture Image")
        }
    }
}
