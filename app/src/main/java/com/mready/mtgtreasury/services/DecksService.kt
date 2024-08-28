package com.mready.mtgtreasury.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.mready.mtgtreasury.models.Deck
import com.mready.mtgtreasury.utility.await
import com.mready.mtgtreasury.utility.awaitOrNull
import com.mready.mtgtreasury.utility.requireUserId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow


@Singleton
class DecksService @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    init {
    }

    suspend fun createDeck(deckName: String, deckImage: String, deckList: HashMap<String, Int>) {
        val userId = auth.requireUserId

        val deck = Deck(uid = userId, deckImage = deckImage, name = deckName, cards = deckList)

        db.collection("decks").document().set(deck).awaitOrNull()
    }

    suspend fun updateDeck(deckId: String, deckName: String, deckImage: String, deckList: HashMap<String, Int>) {
        val userId = auth.requireUserId

        val deck = Deck(uid = userId, deckImage = deckImage, name = deckName, cards = deckList)

        db.collection("decks").document(deckId).set(deck).awaitOrNull()
    }

    suspend fun getDecksFlow(): Flow<List<Deck>> {
        val userId = auth.requireUserId

        return callbackFlow {
            val listener =
                db.collection("decks").whereEqualTo("uid", userId)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            close(error)
                        }
                        Log.d(
                            "InventoryService",
                            "getInventory: ${value?.toObjects(Deck::class.java)?.size}"
                        )

                        if (value != null) {
                            trySendBlocking((value.toObjects(Deck::class.java)))
                            Log.d("InventoryService", "value sent non null")

                        } else {
                            trySendBlocking(emptyList())
                        }
                    }

            awaitClose {
                listener.remove()
            }
        }
    }

    suspend fun getDeck(deckId: String): Deck {
        return db.collection("decks").document(deckId).get().await().toObject<Deck>()!!
    }

    suspend fun deleteDeck(deckId: String) {
        db.collection("decks").document(deckId).delete().awaitOrNull()
    }

    suspend fun addCardsToDeck(deckId: String, deckList: HashMap<String, Int>) {
        db.collection("decks").document(deckId).update("cardIds", deckList).awaitOrNull()
    }

    suspend fun removeCardFromDeck(deckId: String, cardId: String) {
        db.collection("decks").document(deckId).update("cardIds", FieldValue.arrayRemove(cardId))
            .awaitOrNull()
    }
}