package com.mready.mtgtreasury.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun AsyncSvg(
    modifier: Modifier = Modifier,
    uri: String
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = null,
    )
}