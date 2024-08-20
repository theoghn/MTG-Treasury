package com.mready.mtgtreasury.utility

import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T : Any> Task<T>.await(): T = suspendCoroutine { continuation ->
    this.addOnFailureListener {
        continuation.resumeWithException(it)
    }
    this.addOnCanceledListener {
        continuation.resumeWithException(Throwable("Task $this was cancelled normally."))
    }

    this.addOnSuccessListener {
        continuation.resume(it)
    }
}

suspend fun <T : Any> Task<T?>.awaitOrNull(): T? = suspendCoroutine { continuation ->
    this.addOnSuccessListener {
        continuation.resume(it)
    }
    this.addOnFailureListener {
        continuation.resumeWithException(it)
    }
    this.addOnCanceledListener {
        continuation.resumeWithException(Throwable("Task $this was cancelled normally."))
    }
}