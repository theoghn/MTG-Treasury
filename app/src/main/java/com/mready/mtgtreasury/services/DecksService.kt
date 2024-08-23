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

    suspend fun getDecks(): List<Deck> {
        val userId = auth.requireUserId

        val decks = db.collection("decks").whereEqualTo("uid", userId).get().awaitOrNull()

        return decks?.toObjects(Deck::class.java) ?: emptyList()
    }

    suspend fun deleteDeck(deckId: String) {
        db.collection("decks").document(deckId).delete().awaitOrNull()
    }

    suspend fun addCardsToDeck(deckId: String, cardIds: List<String>) {
        db.collection("decks").document(deckId).update("cardIds", FieldValue.arrayUnion(cardIds)).awaitOrNull()
    }

    suspend fun removeCardFromDeck(deckId: String, cardId: String) {
        db.collection("decks").document(deckId).update("cardIds", FieldValue.arrayRemove(cardId)).awaitOrNull()
    }
}