package com.mready.mtgtreasury.models

import com.google.firebase.firestore.DocumentId

data class AppUser(
    @DocumentId val id: String = "",
    val username: String = "",
    val wishlist: List<String> = listOf(),
    val inventory: HashMap<String,Int> = hashMapOf()
)