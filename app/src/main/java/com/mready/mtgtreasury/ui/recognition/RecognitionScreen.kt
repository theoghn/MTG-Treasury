package com.mready.mtgtreasury.ui.recognition

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mready.mtgtreasury.R
import com.mready.mtgtreasury.ui.theme.AccentColor
import com.mready.mtgtreasury.ui.theme.BoxColor
import com.mready.mtgtreasury.utility.captureImage
import com.mready.mtgtreasury.utility.getCameraProvider
import kotlinx.coroutines.launch
import java.io.IOException

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
    val coroutineScope = rememberCoroutineScope()
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

    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.launch {
                val cameraProvider = context.getCameraProvider()
                cameraProvider.unbindAll()
            }
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
                onBack = {
                    onBack()
                },
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
    val lensFacing = CameraSelector.DEFAULT_BACK_CAMERA
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY).build()
    val preview = Preview.Builder().setResolutionSelector(resolutionSelector).build()
    val scaleType = PreviewView.ScaleType.FIT_CENTER

    val previewView = remember {
        PreviewView(context).apply {
            this.scaleType = scaleType
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    LaunchedEffect(shouldBindCamera) {
        if (shouldBindCamera) {
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, lensFacing, preview, imageCapture)
            preview.setSurfaceProvider(previewView.surfaceProvider)
            updateShouldBindCamera(false)
            hideCircularLoading()
        }
    }

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

    var isDialogVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .aspectRatio(9f / 16f)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
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
                )
            }

            IconButton(
                onClick = { onBack() }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp),
                text = stringResource(R.string.scan),
                fontSize = 20.sp,
                color = Color.White
            )

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = {
                    isDialogVisible = true
                }
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        if (isCircularLoadingVisible) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center),
                color = Color.White
            )
        }
    }

    if(isDialogVisible){
        MinimalDialog { isDialogVisible = false }
    }
}


@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BoxColor)
        ) {
            Text(
                text = "How to scan",
                modifier = Modifier
                    .padding(top = 12.dp)
                    .align(Alignment.CenterHorizontally),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = AccentColor
                )

                Text(
                    modifier = Modifier.width(230.dp),
                    text = "Avoid direct lighting on the card",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = AccentColor
                )

                Text(
                    modifier = Modifier.width(230.dp),
                    text = "Align the card vertically with the frame",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    tint = AccentColor
                )

                Text(
                    modifier = Modifier.width(230.dp),
                    text = "Try different environments",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    color = Color.White
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = { onDismissRequest() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "Got it",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    color = Color.White
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun Preview(modifier: Modifier = Modifier) {
    MinimalDialog { }
}



//test