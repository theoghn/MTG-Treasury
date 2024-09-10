package com.mready.mtgtreasury.ui.recognition

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mready.mtgtreasury.R
import java.io.IOException

@Composable
fun RecognitionScreen(
    viewModel: RecognitionViewModel = hiltViewModel()
) {
    val bestImageId by viewModel.bestImageId.collectAsState()

    var imageUri by remember {
        mutableStateOf<Uri>(Uri.EMPTY)
    }
    var image by remember {
        mutableStateOf<InputImage?>(null)
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
                            context = context,
                            imageUri = imageUri
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