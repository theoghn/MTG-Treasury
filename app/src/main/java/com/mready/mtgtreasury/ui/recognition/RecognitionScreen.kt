package com.mready.mtgtreasury.ui.recognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.opencv.android.Utils.bitmapToMat
import org.opencv.core.Core
import org.opencv.core.Core.NORM_MINMAX
import org.opencv.core.Core.normalize
import java.io.IOException
import org.opencv.core.Mat
import org.opencv.core.MatOfFloat
import org.opencv.core.MatOfInt
import org.opencv.imgproc.Imgproc.COLOR_BGR2HSV
import org.opencv.imgproc.Imgproc.HISTCMP_CORREL
import org.opencv.imgproc.Imgproc.calcHist
import org.opencv.imgproc.Imgproc.compareHist
import org.opencv.imgproc.Imgproc.cvtColor
import java.io.InputStream

@Composable
fun RecognitionScreen(
    viewModel: RecognitionViewModel = hiltViewModel()
) {
    val bestImageId by viewModel.bestImageId.collectAsState()

    var uri by remember {
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
                uri = it
                try {
                    image = InputImage.fromFilePath(context, uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    )
    var selectedImages by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 2),
        onResult = { uris -> selectedImages = uris }
    )

    var imageText by remember {
        mutableStateOf("")
    }

    var similarity by remember {
        mutableStateOf("0.0")
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

        Button(onClick = {
            if (image != null) {
                val result = recognizer.process(image!!)
                    .addOnSuccessListener { visionText ->
//                        imageText = visionText.text.split("\n")[0]
                        imageText = visionText.text
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        // ...
                    }
            }
        }) {
            Text("Process Image")
        }

        Button(onClick = {
//            val x = viewModel.loadImage(context, selectedImages[0])
//            val y = viewModel.loadImage(context, selectedImages[1])
//            if (x == null || y == null) {
//                println("Histogram Similarity: 00000")
//                return@Button
//            }
            viewModel.searchCards(
                name = imageText,
                manaCost = emptyList(),
                colors = emptyList(),
                rarity = emptyList(),
                type = emptyList(),
                superType = emptyList(),
                context = context,
                imgUri = uri
            )

        }) {
            Text("Search Images")
        }



        Text(text = "Similarity $similarity", color = Color.White)

        Text(text = imageText, color = Color.White)


        AsyncImage(
            model = bestImageId,
            modifier = Modifier
                .padding(12.dp)
                .width(169.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Transparent)
            ,
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.card_back),
            error = painterResource(id = R.drawable.card_back)
        )

    }


// Run inference
}

