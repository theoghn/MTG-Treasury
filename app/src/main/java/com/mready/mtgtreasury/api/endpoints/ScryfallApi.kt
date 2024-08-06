package com.mready.mtgtreasury.api.endpoints

import com.mready.mtgtreasury.api.ScryfallApiClient
import com.mready.mtgtreasury.models.MtgSet
import com.mready.mtgtreasury.models.card.MtgCard
import com.mready.mtgtreasury.models.card.CardImageUris
import com.mready.mtgtreasury.models.card.CardLegalities
import com.mready.mtgtreasury.models.card.CardPrices
import net.mready.apiclient.get
import net.mready.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScryfallApi @Inject constructor(
    private val apiClient: ScryfallApiClient
) {
    suspend fun getCard(): MtgCard {
        return apiClient.get(
            endpoint = "cards/random"
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

}

private fun Json.toCard() = MtgCard(
    name = this["name"].string,
    releaseDate = this["released_at"].string,
    manaCost = this["mana_cost"].stringOrNull ?: "",
    type = this["type_line"].string,
    oracleText = this["oracle_text"].stringOrNull ?: "",
    power = this["power"].stringOrNull,
    colors = this["colors"].arrayOrNull?.map { it.string } ?: emptyList(),
    imageUris = this["image_uris"].toCardUris(),
    foil = this["foil"].boolOrNull ?: false,
    edhRank = this["edhrec_rank"].intOrNull ?: 0,
    setAbbreviation = this["set"].string,
    setName = this["set_name"].string,
    prices = this["prices"].toCardPrices(),
    legalities = this["legalities"].toCardLegalities()
)

private fun Json.toCardUris() = CardImageUris(
    borderCrop = this["border_crop"].string,
    artCrop = this["art_crop"].string,
    normalSize = this["normal"].string,
    largeSize = this["large"].string,
    smallSize = this["small"].string
)

private fun Json.toCardLegalities() = CardLegalities(
    standard = this["standard"].string,
//    future = this["future"].string,
    historic = this["historic"].string,
    timeless = this["timeless"].string,
//    gladiator = this["gladiator"].string,
    pioneer = this["pioneer"].string,
    explorer = this["explorer"].string,
    modern = this["modern"].string,
    legacy = this["legacy"].string,
    pauper = this["pauper"].string,
    vintage = this["vintage"].string,
    penny = this["penny"].string,
    commander = this["commander"].string,
    oathBreaker = this["oathbreaker"].string,
//    standardBrawl = this["standardbrawl"].string,
    brawl = this["brawl"].string,
    alchemy = this["alchemy"].string,
//    pauperCommander = this["paupercommander"].string,
//    duel = this["duel"].string,
//    oldSchool = this["oldschool"].string,
//    preModern = this["premodern"].string,
//    predh = this["predh"].string
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
    cardCount = this["card_count"].int
)