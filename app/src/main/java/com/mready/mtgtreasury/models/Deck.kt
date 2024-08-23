package com.mready.mtgtreasury.models

import com.google.firebase.firestore.DocumentId

data class Deck(
    @DocumentId val id: String = "",
    val uid: String = "",
    val name: String = "",
    val cardIds: List<String> = listOf()
)