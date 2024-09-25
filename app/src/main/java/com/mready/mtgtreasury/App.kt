package com.mready.mtgtreasury

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.request.CachePolicy
import coil.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(applicationContext)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .logger(DebugLogger())
            .build()
}