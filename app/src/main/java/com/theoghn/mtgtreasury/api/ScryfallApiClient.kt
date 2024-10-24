package com.theoghn.mtgtreasury.api

import com.theoghn.mtgtreasury.BuildConfig
import net.mready.apiclient.ApiClient
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScryfallApiClient @Inject constructor(
    httpClient: OkHttpClient
) : ApiClient(httpClient = httpClient, baseUrl = BuildConfig.API_HOST)