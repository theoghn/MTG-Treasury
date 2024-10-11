package com.mready.mtgtreasury.ui.recognition

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.theme.AccentColor
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun RecognitionScreen(
    viewModel: RecognitionViewModel = hiltViewModel(),
    onBack: () -> Boolean,
    onNavigateToCard: (String) -> Unit
) {
    val matchedCardId by viewModel.matchCardId.collectAsState()
    var image by remember { mutableStateOf<InputImage?>(null) }
    var imageText by remember { mutableStateOf("") }
    var shouldBindCamera by remember { mutableStateOf(true) }
    var isCircularLoadingVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(matchedCardId) {
        if (matchedCardId.isNotEmpty()) {
            onNavigateToCard(matchedCardId)
            viewModel.resetMatchCardId()
        }
    }



    LaunchedEffect(image) {
        if (image != null) {
            Log.d("RecognitionScreen", "2nd camera provider unbind")
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            isCircularLoadingVisible = true

            recognizer.process(image!!)
                .addOnSuccessListener { visionText ->
                    imageText = visionText.text
                        .split("\n")[0]
                        .replace(("[^\\w\\d ]").toRegex(), "")
                        .split(" ")
                        .take(3)
                        .joinToString(" ")

                    if (imageText.isNotEmpty()) {
                        viewModel.searchCards(
                            name = imageText,
                            image = image!!
                        )
                    } else {
                        shouldBindCamera = true
                      }
                    Log.d("RecognitionScreen : LaunchedEffect", "Image Text: $imageText")
                }
                .addOnFailureListener {
                    isCircularLoadingVisible = false
                    Log.e("RecognitionScreen : LaunchedEffect", "Failed to process image")
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreviewScreen(
                shouldBindCamera = shouldBindCamera,
                isCircularLoadingVisible = isCircularLoadingVisible,
                updateImage = { it: InputImage ->
                    image = it
                },
                onBack = { onBack() },
                updateShouldBindCamera = { it: Boolean ->
                    shouldBindCamera = it
                },
                hideCircularLoading = {
                    isCircularLoadingVisible = false
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { launcher.launch(Manifest.permission.CAMERA) }) {
                    Text("Request Camera Permission")
                }
            }
        }
    }
}

@Composable
fun ProcessImage(
    viewModel: RecognitionViewModel,
    incomingImage: InputImage
) {
    val bestImageId by viewModel.bestImageId.collectAsState()

    var imageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }

    var image by remember {
        mutableStateOf<InputImage?>(incomingImage)
    }
    val context = LocalContext.current
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            if (it != null) {
                imageUri = it
                try {
                    image = InputImage.fromFilePath(context, imageUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    )

//    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2),
//        onResult = { uris -> selectedImages = uris }
//    )

    var imageText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(image) {
        Log.d("RecognitionScreen : LaunchedEffect", "Image Text: $imageText")

        if (image != null) {
            recognizer.process(image!!)
                .addOnSuccessListener { visionText ->
                    imageText = visionText.text
                        .split("\n")[0]
                        .replace(("[^\\w\\d ]").toRegex(), "")
                        .split(" ")
                        .take(3)
                        .joinToString(" ")

                    if (imageText.isNotEmpty()) {
                        viewModel.searchCards(
                            name = imageText,
                            image = incomingImage
                        )
                    }
                }
                .addOnFailureListener {
                    Log.e("RecognitionScreen : LaunchedEffect", "Failed to process image")
                }
        }
    }



    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("Load Images")
        }

//        Button(onClick = {
//            if (image != null) {
//                val result = recognizer.process(image!!)
//                    .addOnSuccessListener { visionText ->
//                        imageText = visionText.text.split("\n")[0].replace("[^A-Za-z]", "")
////                        imageText = visionText.text
//                    }
//                    .addOnFailureListener { e ->
//                        // Task failed with an exception
//                        // ...
//                    }
//            }
//        }) {
//            Text("Process Image")
//        }
//
//        Button(onClick = {
//            viewModel.searchCards(
//                name = imageText,
//                context = context,
//                imageUri = imageUri
//            )
//
//        }) {
//            Text("Search Images")
//        }

        Text(text = imageText, color = Color.White)


        AsyncImage(
            model = bestImageId,
            modifier = Modifier
                .padding(12.dp)
                .width(169.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Transparent),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.card_back),
            error = painterResource(id = R.drawable.card_back)
        )
    }
}


@Composable
fun CameraPreviewScreen(
    shouldBindCamera: Boolean,
    isCircularLoadingVisible: Boolean,
    updateImage: (InputImage) -> Unit,
    onBack: () -> Boolean,
    updateShouldBindCamera: (Boolean) -> Unit,
    hideCircularLoading: () -> Unit
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }
    LaunchedEffect(shouldBindCamera) {
        if (shouldBindCamera) {
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
            preview.setSurfaceProvider(previewView.surfaceProvider)
            updateShouldBindCamera(false)
            hideCircularLoading()
        }
    }

//    LaunchedEffect(lensFacing) {
//        val cameraProvider = context.getCameraProvider()
//        cameraProvider.unbindAll()
//        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//    }

    var isPressed by remember { mutableStateOf(false) }

    val paddingAnimation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 6.dp,
        animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    val colorAnimation by animateColorAsState(
        targetValue = if (isPressed) AccentColor else Color.White,
        animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 32.dp),
            onClick = { onBack() }
        ) {
            Icon(
                modifier = Modifier.size(30.dp),
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        if (isCircularLoadingVisible) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color.White
            )
        }

        Box(
            modifier = Modifier
                .padding(bottom = 48.dp)
                .size(60.dp)
                .border(2.dp, Color.White, CircleShape)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            val x = tryAwaitRelease()
                            if (x) {
                                isPressed = false


                                captureImage(
                                    imageCapture = imageCapture,
                                    context = context,
                                    updateImage = updateImage,
                                )
                            } else {
                                isPressed = false
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(paddingAnimation)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(colorAnimation)
            ) {

            }
        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private fun captureImage(
    imageCapture: ImageCapture,
    context: Context,
    updateImage: (InputImage) -> Unit
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(image: ImageProxy) {
                val mediaImage = image.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(
                        mediaImage,
                        image.imageInfo.rotationDegrees
                    )
                    updateImage(inputImage)
                    Log.d("RecognitionScreen", "1st camera provider unbind")
                }
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                println("Failed $exception")
            }
        }
    )
}