package com.mready.mtgtreasury.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.mready.mtgtreasury.R


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
//        colorFilter = ColorFilter.tint(Color.White),
        contentDescription = null,
//        placeholder = painterResource(id = R.drawable.filter_multiple)
    )
}