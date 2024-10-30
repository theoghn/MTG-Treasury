package com.theoghn.mtgtreasury.services

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.theoghn.mtgtreasury.models.AppUser
import com.theoghn.mtgtreasury.utility.await
import com.theoghn.mtgtreasury.utility.awaitOrNull
import com.theoghn.mtgtreasury.utility.requireUserId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class InventoryService @Inject constructor(
    private val cardsService: CardsService
) {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    suspend fun updateCardQuantity(cardId: String, quantity: Int) {
        val userId = auth.requireUserId
        val userDoc = db.collection("users").document(userId)

        if (quantity > 0) {
            userDoc.update("inventory.$cardId", quantity).awaitOrNull()
        }
        else{
            userDoc.update("inventory.$cardId", FieldValue.delete()).awaitOrNull()
        }
    }

    suspend fun addCardToInventory(cardId: String) {
        val userId = auth.requireUserId

        val userDoc = db.collection("users").document(userId)
//        userDoc.update("inventory", FieldValue.arrayUnion(cardId)).awaitOrNull()
        userDoc.update("inventory.$cardId", FieldValue.increment(1)).awaitOrNull()
    }

    suspend fun removeCardFromInventory(cardId: String, currentQuantity: Int) {
        val userId = auth.requireUserId

        val userDoc = db.collection("users").document(userId)
//        userDoc.update("inventory", FieldValue.arrayRemove(cardId)).awaitOrNull()
        if (currentQuantity > 1) {
            userDoc.update("inventory.$cardId", FieldValue.increment(-1)).awaitOrNull()
            return
        }
        else{
            userDoc.update("inventory.$cardId", FieldValue.delete()).awaitOrNull()
        }
    }

    suspend fun getInventoryFlow(): Flow<HashMap<String,Int>> {
        val userId = auth.requireUserId

        return callbackFlow {
            val listener =
                db.collection("users").document(userId).addSnapshotListener { value, error ->
                    if (error != null) {
                        close(error)
                    }
                    Log.d("InventoryService", "getInventory: ${value?.toObject<AppUser>()?.inventory?.size}")
                    if(value?.toObject<AppUser>() != null){
                        trySendBlocking((value.toObject<AppUser>()!!.inventory))
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    suspend fun getInventory(): HashMap<String, Int> {
        val userId = auth.requireUserId

        return db.collection("users").document(userId).get().await().toObject<AppUser>()!!.inventory
    }

    suspend fun calculateInventoryValue(inventory: HashMap<String, Int>): Double {
        var totalValue = 0.0
        val chunkedList = inventory.keys.chunked(60)

        chunkedList.forEach {
            val cards = cardsService.getCardsByIds(it)
            for (card in cards) {
                totalValue += card.prices.eur.toFloat() * inventory[card.id]!!
            }
        }

        return totalValue
    }
}