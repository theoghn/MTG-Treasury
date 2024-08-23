package com.mready.mtgtreasury.services

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.mready.mtgtreasury.models.Deck
import com.mready.mtgtreasury.utility.awaitOrNull
import com.mready.mtgtreasury.utility.requireUserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DecksService @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    init {
        CoroutineScope(Dispatchers.IO).launch {
            createDeck("test", listOf("1", "2", "3"))
        }
    }

    suspend fun createDeck(deckName: String, deckList: List<String>) {
        val userId = auth.requireUserId

        val deck = Deck(uid = userId, name = deckName, cardIds = deckList)

        db.collection("decks").document().set(deck).awaitOrNull()

    }


}