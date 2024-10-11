package com.mready.mtgtreasury.ui.recognition

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mready.mtgtreasury.services.CardsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class RecognitionViewModel @Inject constructor(
    private val cardsService: CardsService,
) : ViewModel() {
    val bestImageId = MutableStateFlow("")
    val matchCardId = MutableStateFlow("")

    fun resetMatchCardId() = matchCardId.update { "" }

    fun searchCards(
        name: String,
        image: InputImage
    ) {
        viewModelScope.launch {
            val cards = cardsService.getCardsByName(
                name = name,
            )

            delay(100)

            var maxMatch = 0.0
            var bestMatchImgId = ""
            var bestMatchCardId = ""

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            var imageText = ""

            recognizer.process(image).addOnSuccessListener {
                imageText = it.text.split("\n").takeLast(4).joinToString(" ")
                Log.d("RecognitionViewModel", "InitialImage: $imageText")

            }
            Log.d("RecognitionViewModel", "Name: $name")



            if (cards.isNotEmpty()) {
                cards.forEachIndexed { index, card ->
                    val imageByteArray =
                        createBitmapFromUri(card.imageUris.normalSize) ?: return@forEachIndexed
                    val cardImage = InputImage.fromBitmap(imageByteArray, 0)
                    recognizer.process(cardImage).addOnSuccessListener {
                        val cardText = it.text.split("\n").takeLast(4).joinToString(" ")
                        val match = jaroWinklerSimilarity(imageText, cardText)
                        Log.d("RecognitionViewModel", "Card: ${card.imageUris.borderCrop}")
                        Log.d(
                            "RecognitionViewModel",
                            "Card Text: $cardText \n Image Text: $imageText \n match = $match"
                        )
                        if (match > maxMatch) {
                            maxMatch = match
                            bestMatchImgId = card.imageUris.normalSize
                            bestMatchCardId = card.id
                        }
                    }.addOnCompleteListener {
                        if (index == cards.size - 1) {
                            Log.d(
                                "RecognitionViewModel",
                                "updated the best image uri: $bestMatchImgId"
                            )
                            bestImageId.update { bestMatchImgId }
                            matchCardId.update { bestMatchCardId }
                        }
                    }
                }
            }
        }
    }

    private suspend fun createBitmapFromUri(imageUrl: String): Bitmap? {
        try {
            var resultBytes: Bitmap? = null
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()

                val request = Request.Builder().url(imageUrl).build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw IOException("Failed to download file: $response")
                }
                val imageBytes = response.body?.byteStream()?.readBytes() ?: return@withContext null
                val imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                resultBytes = imageBitmap
            }

            return resultBytes
        } catch (e: Exception) {
            Log.d("RecognitionViewModel", "createBitmapFromUri() : Error loading image from URL")
            e.printStackTrace()
            return null
        }
    }
}

fun jaroWinklerSimilarity(s1: String, s2: String): Double {
    val jaroSimilarity = jaroSimilarity(s1, s2)
    val prefixLength = commonPrefixLength(s1, s2)

    val scalingFactor = 0.1

    return jaroSimilarity + prefixLength * scalingFactor * (1 - jaroSimilarity)
}


fun jaroSimilarity(s1: String, s2: String): Double {
    if (s1 == s2) return 1.0

    val maxDistance = (s1.length.coerceAtLeast(s2.length) / 2) - 1
    val s1Matches = BooleanArray(s1.length)
    val s2Matches = BooleanArray(s2.length)

    var matches = 0
    var transpositions = 0

    for (i in s1.indices) {
        val start = 0.coerceAtLeast(i - maxDistance)
        val end = (i + maxDistance + 1).coerceAtMost(s2.length)

        for (j in start until end) {
            if (s2Matches[j]) continue
            if (s1[i] != s2[j]) continue
            s1Matches[i] = true
            s2Matches[j] = true
            matches++
            break
        }
    }

    if (matches == 0) return 0.0


    var k = 0
    for (i in s1.indices) {
        if (!s1Matches[i]) continue
        while (!s2Matches[k]) k++
        if (s1[i] != s2[k]) transpositions++
        k++
    }

    transpositions /= 2

    return (matches / s1.length.toDouble() + matches / s2.length.toDouble() + (matches - transpositions) / matches.toDouble()) / 3.0
}

fun commonPrefixLength(s1: String, s2: String): Int {
    var prefixLength = 0

    for (i in 0 until s1.length.coerceAtMost(s2.length)) {
        if (s1[i] == s2[i]) {
            prefixLength++
        } else {
            break
        }
    }

    return prefixLength
}
