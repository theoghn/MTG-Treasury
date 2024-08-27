package com.mready.mtgtreasury.models

import com.google.firebase.firestore.DocumentId

data class AppUser(
    @DocumentId val id: String = "",
    val username: String = "",
    val inventoryValue : Float = 0.0F,
    val wishlist: List<String> = listOf(),
    val inventory: HashMap<String,Int> = hashMapOf()
)