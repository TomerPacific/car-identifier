package com.tomerpacific.caridentifier.composable

import android.net.Uri
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.tomerpacific.caridentifier.CameraFileUtils.takePicture
import com.tomerpacific.caridentifier.R
import com.tomerpacific.caridentifier.model.CarViewModel
import com.tomerpacific.caridentifier.model.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    navController: NavController,
    carViewModel: CarViewModel,
) {
    carViewModel.resetData()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(executor) {
        onDispose {
            executor.shutdown()
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
        }
    }.apply {
        isTapToFocusEnabled = true
    }

    LaunchedEffect(Unit) {
        carViewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        CameraContent(
            contentPadding = contentPadding,
            cameraController = cameraController,
            executor = executor,
            navController = navController,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
private fun CameraContent(
    contentPadding: PaddingValues,
    cameraController: LifecycleCameraController,
    executor: ExecutorService,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_START
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    controller = cameraController
                }
            },
            onRelease = {
                cameraController.unbind()
            },
        )
        CaptureButton(
            cameraController = cameraController,
            executor = executor,
            navController = navController,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
private fun CaptureButton(
    cameraController: LifecycleCameraController,
    executor: ExecutorService,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Button(
        onClick = {
            takePicture(cameraController, context, executor, { uri ->
                coroutineScope.launch(Dispatchers.Main) {
                    navController.navigate(Screen.VerifyPhoto.route + "/${Uri.encode(uri.toString())}")
                }
            }, { imageCaptureException ->
                Log.e("CameraPreview", "Error capturing image", imageCaptureException)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error capturing image ${imageCaptureException.message}")
                }
                navController.popBackStack()
            })
        },
        modifier = Modifier.padding(bottom = 15.dp)
    ) {
        Icon(
            painterResource(R.drawable.ic_camera),
            contentDescription = "Capture Image",
            modifier = Modifier.size(50.dp),
        )
    }
}
