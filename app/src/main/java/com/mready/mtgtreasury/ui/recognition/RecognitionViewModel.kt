package com.mready.mtgtreasury.ui.recognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import com.mready.mtgtreasury.services.CardsService
import com.mready.mtgtreasury.services.InventoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.opencv.android.Utils.bitmapToMat
import org.opencv.core.Core
import org.opencv.core.Core.NORM_MINMAX
import org.opencv.core.Core.normalize
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.core.MatOfFloat
import org.opencv.core.MatOfInt
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc.COLOR_BGR2HSV
import org.opencv.imgproc.Imgproc.HISTCMP_CORREL
import org.opencv.imgproc.Imgproc.HISTCMP_INTERSECT
import org.opencv.imgproc.Imgproc.calcHist
import org.opencv.imgproc.Imgproc.compareHist
import org.opencv.imgproc.Imgproc.cvtColor
import java.io.IOException
import java.io.InputStream
import java.net.URL
import javax.inject.Inject


@HiltViewModel
class RecognitionViewModel @Inject constructor(
    private val cardsService: CardsService,
    private val inventoryService: InventoryService
) : ViewModel() {
    val bestImageId = MutableStateFlow("")
//    val init = MutableStateFlow(false)

    fun searchCards(
        name: String,
        manaCost: List<String>,
        colors: List<String>,
        rarity: List<String>,
        type: List<String>,
        superType: List<String>,
        context: Context,
        imgUri: Uri
    ) {
        viewModelScope.launch {
            val cards = cardsService.getCardsByFilters(
                name = name,
                manaCost = manaCost,
                colors = colors,
                rarity = rarity,
                type = type,
                superType = superType,
            )

//                cards = cards.map {
//                    it.copy(
//                        qty = if (inventory.contains(it.id)) {
//                            inventory[it.id]!!
//                        } else {
//                            0
//                        }
//                    )
//                }

            delay(100)

            val image = loadImage(context, imgUri)
//            val image = loadImageFromURL("https://cards.scryfall.io/normal/front/c/d/cd52d335-337c-4ac8-8854-15850e2da093.jpg?1696168625")
            var maxMatch = 0.0
            var bestMatchImgId = ""

            if (cards.isNotEmpty()) {
                cards.forEach { card ->
                    val cardImage = loadImageFromURL(card.imageUris.normalSize)
                    if (image != null && cardImage != null) {
                        val match = compareHistograms(image, cardImage)
                        Log.d("RecognitionViewModel", "Match: $match ${card.imageUris.normalSize}")
                        if (match > maxMatch) {
                            maxMatch = match
                            bestMatchImgId = card.imageUris.normalSize
                        }
                    }
                }
                bestImageId.update { bestMatchImgId }
            }
        }
    }

    suspend fun loadImageFromURL(imageUrl: String): Mat? {
        try {
            var matOfByte: Mat? = null
            // Build the request to fetch the image
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()

                val request = Request.Builder().url(imageUrl).build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw IOException("Failed to download file: $response")
                }
                val imageBytes = response.body?.byteStream()?.readBytes() ?: return@withContext null

                // Create a MatOfByte from the image bytes
                matOfByte = MatOfByte(*imageBytes)

                // Convert the MatOfByte to a Mat (OpenCV matrix)
            }

            // Execute the request

            // Check if response was successful


            // Read the image as bytes
            if (matOfByte == null) {
                return null
            }
            return Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR)

//            return mat
        } catch (e: Exception) {
            Log.d("RecognitionViewModel", "Error loading image from URL")
            e.printStackTrace()
            return null
        }
    }

    fun loadImage(context: Context, uri: Uri): Mat? {
        try {
            // Get input stream from the Uri
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            val mat = Mat()
            // Convert Bitmap to OpenCV Mat
            bitmapToMat(bitmap, mat)
            return mat
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    fun calculateHistogram(image: Mat): Mat {
        val hsvBase = Mat()
        cvtColor(image, hsvBase, COLOR_BGR2HSV)

        val hist = Mat()
        val histSize = MatOfInt(50, 60)
        val ranges = MatOfFloat(0f, 180f, 0f, 256f)

        val bgrPlanes: MutableList<Mat> = mutableListOf()
        Core.split(image, bgrPlanes);

        calcHist(
            arrayOf(image).toMutableList(),
            MatOfInt(0, 1),
            Mat(),
            hist,
            histSize,
            ranges
        )
        normalize(hist, hist, 0.0, 1.0, NORM_MINMAX, -1, Mat())
        return hist
    }

    fun compareHistograms(img1: Mat, img2: Mat): Double {
        val hist1 = calculateHistogram(img1)
        val hist2 = calculateHistogram(img2)

        return compareHist(hist1, hist2, HISTCMP_INTERSECT)
    }
}