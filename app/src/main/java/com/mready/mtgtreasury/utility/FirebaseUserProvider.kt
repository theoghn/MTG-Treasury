package com.mready.mtgtreasury.utility

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

val FirebaseAuth.requireUser: FirebaseUser
    get() = currentUser ?: throw IllegalStateException("User not logged in")

val FirebaseAuth.requireUserId: String
    get() = requireUser.uid