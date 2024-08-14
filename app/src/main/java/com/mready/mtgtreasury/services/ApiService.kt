package com.mready.mtgtreasury.services

import com.mready.mtgtreasury.api.ScryfallApiClient
import com.mready.mtgtreasury.api.endpoints.ScryfallApi
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApiService @Inject constructor(
    private val apiClient: ScryfallApi
) {
    suspend fun getRandomCard() = apiClient.getRandomCard()
    suspend fun getCard(id: String) = apiClient.getCard(id)
    suspend fun getMostValuableCards() = apiClient.getMostValuableCards()
    suspend fun getNewestSets() = apiClient.getNewestSets()
}