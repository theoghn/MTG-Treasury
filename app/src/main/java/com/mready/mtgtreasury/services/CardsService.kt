package com.mready.mtgtreasury.services

import com.mready.mtgtreasury.api.endpoints.ScryfallApi
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CardsService @Inject constructor(
    private val apiClient: ScryfallApi
) {
    suspend fun getRandomCard() = apiClient.getRandomCard()
    suspend fun getCard(id: String) = apiClient.getCard(id)
    suspend fun getMostValuableCards() = apiClient.getMostValuableCards()
    suspend fun getNewestSets() = apiClient.getNewestSets()
    suspend fun getCardSuggestions(query: String) = apiClient.getCardSuggestions(query)
    suspend fun getCardsByFilters(
        name: String,
        manaCost: List<String>,
        colors: List<String>,
        rarity: List<String>,
        type: List<String>,
        superType: List<String>,
    ) = apiClient.getCardsByFilters(
        name = name,
        manaCost = manaCost,
        colors = colors,
        rarity = rarity,
        type = type,
        superType = superType,
    )
    suspend fun getCardsByIds(ids: List<String>) = apiClient.getCardsByIds(ids)
}