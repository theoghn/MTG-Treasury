package com.mready.mtgtreasury.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val username: String = "",
    val wishlist: List<String> = listOf(),
    val inventory: List<String> = listOf()
)