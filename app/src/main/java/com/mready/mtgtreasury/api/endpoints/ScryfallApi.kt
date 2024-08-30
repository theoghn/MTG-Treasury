package com.mready.mtgtreasury.api.endpoints

import android.util.Log
import com.mready.mtgtreasury.api.ScryfallApiClient
import com.mready.mtgtreasury.models.MtgSet
import com.mready.mtgtreasury.models.card.CardImageUris
import com.mready.mtgtreasury.models.card.CardLegalities
import com.mready.mtgtreasury.models.card.CardPrices
import com.mready.mtgtreasury.models.card.MtgCard
import net.mready.apiclient.get
import net.mready.apiclient.jsonObjectBody
import net.mready.apiclient.post
import net.mready.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScryfallApi @Inject constructor(
    private val apiClient: ScryfallApiClient
) {
    suspend fun getRandomCard(): MtgCard {
        return apiClient.get(
            endpoint = "cards/random"
        ) { json ->
            json.toCard()
        }
    }

    suspend fun getCard(id: String): MtgCard {
        return apiClient.get(
            endpoint = "cards/$id"
        ) { json ->
            json.toCard()
        }
    }

    suspend fun getMostValuableCards(): List<MtgCard> {
        return apiClient.get(
            endpoint = "cards/search",
            query = mapOf("q" to "eur>2000", "order" to "eur")
        ) { json ->
            json["data"].array.map { it.toCard() }.take(3)
        }
    }

    suspend fun getNewestSets(): List<MtgSet> {
        return apiClient.get(
            endpoint = "sets",
        ) { json ->
            json["data"].array
                .map { it.toSet() }
                .filter { it.iconUri != "" }
                .take(6)
        }
    }

    suspend fun getCardSuggestions(query: String): List<String> {
        Log.d("ScryfallApi", "getCardSuggestions: $query")
        return apiClient.get(
            endpoint = "cards/autocomplete",
            query = mapOf("q" to query)
        ) { json ->
            json["data"].array.map { it.string }.take(6)
        }
    }

    suspend fun getCardsByIds(ids: List<String>): List<MtgCard> {
        try {
            return apiClient.post(
                endpoint = "cards/collection",
                body = jsonObjectBody {
                    obj["identifiers"] = jsonArray {
                        ids.forEach {
                            array += jsonObject {
                                obj["id"] = it
                            }
                        }
                    }
                }
            ) { json ->
                json["data"].array.map { it.toCard() }
            }
        } catch (e: Exception) {
            return emptyList()
        }

    }

    suspend fun getCardsByFilters(
        name: String,
        type: List<String>,
        superType: List<String>,
        colors: List<String>,
        rarity: List<String>,
        manaCost: List<String>,
    ): List<MtgCard> {
        return try {
            apiClient.get(
                endpoint = "cards/search",
                query = mapOf(
                    "q" to buildSearchQuery(name, type, superType, colors, rarity, manaCost),
                )
            ) { json ->
                json["data"].array.map { it.toCard() }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun buildSearchQuery(
        name: String,
        type: List<String>,
        superType: List<String>,
        colors: List<String>,
        rarity: List<String>,
        manaCost: List<String>
    ): String {
        val query = buildString {
            if (type.isEmpty() && superType.isEmpty() && colors.isEmpty() && rarity.isEmpty() && manaCost.isEmpty()) {
                append("name:$name")
            } else {
                if (name.isNotEmpty()){
                    append("name:$name")
                }
            }

            append(" unique:art")
            type.forEach { append(" type:$it") }
            superType.forEach { append(" type:$it") }
            colors.forEach { append(" c:$it") }
            rarity.forEach { append(" r:$it") }
            manaCost.forEach { append(" mana:$it") }
        }
        return query
    }
}

private fun Json.toCard() = MtgCard(
    id = this["id"].string,
    name = this["name"].string,
    releaseDate = this["released_at"].string,
    manaCost = this["mana_cost"].stringOrNull ?: "",
    type = this["type_line"].stringOrNull ?: "",
    oracleText = this["oracle_text"].stringOrNull ?: "This is a simple card",
    power = this["power"].stringOrNull,
    colors = this["colors"].arrayOrNull?.map { it.string } ?: emptyList(),
    imageUris = this["image_uris"].toCardUris(),
    foil = this["foil"].boolOrNull ?: false,
    edhRank = this["edhrec_rank"].intOrNull ?: 0,
    setAbbreviation = this["set"].string,
    setName = this["set_name"].string,
    artist = this["artist"].stringOrNull ?: "unknown",
    prices = this["prices"].toCardPrices(),
    legalities = this["legalities"].toCardLegalities()
)

private fun Json.toCardUris() = CardImageUris(
    borderCrop = this["border_crop"].stringOrNull ?: "",
    artCrop = this["art_crop"].stringOrNull ?: "",
    normalSize = this["normal"].stringOrNull ?: "",
    largeSize = this["large"].stringOrNull ?: "",
    smallSize = this["small"].stringOrNull ?: ""
)

private fun Json.toCardLegalities() = CardLegalities(
    standard = this["standard"].string,
    historic = this["historic"].string,
    timeless = this["timeless"].string,
    pioneer = this["pioneer"].string,
    explorer = this["explorer"].string,
    modern = this["modern"].string,
    legacy = this["legacy"].string,
    pauper = this["pauper"].string,
    vintage = this["vintage"].string,
    penny = this["penny"].string,
    commander = this["commander"].string,
    oathBreaker = this["oathbreaker"].string,
    brawl = this["brawl"].string,
    alchemy = this["alchemy"].string,
)

private fun Json.toCardPrices() = CardPrices(
    usd = this["usd"].stringOrNull ?: "0.0",
    usdFoil = this["usd_foil"].stringOrNull ?: "0.0",
    eur = this["eur"].stringOrNull ?: "0.0",
    eurFoil = this["eur_foil"].stringOrNull ?: "0.0",
    tix = this["tix"].stringOrNull ?: "0.0",
    usdEtched = this["usd_etched"].stringOrNull ?: "0.0"
)

private fun Json.toSet() = MtgSet(
    name = this["name"].string,
    releaseDate = this["released_at"].string,
    iconUri = this["icon_svg_uri"].string,
    cardCount = this["card_count"].int,
    scryfall_uri = this["scryfall_uri"].stringOrNull ?: ""
)