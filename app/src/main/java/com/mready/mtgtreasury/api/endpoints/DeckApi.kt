package com.mready.mtgtreasury.api.endpoints

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckApi @Inject constructor() {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
}