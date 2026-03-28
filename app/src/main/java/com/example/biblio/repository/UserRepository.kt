package com.example.biblio.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = Firebase.firestore

    suspend fun upsertUser(user: FirebaseUser) {
        val ref = db.collection("users").document(user.uid)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val data = hashMapOf<String, Any>(
                "name" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!snap.exists()) {
                data["createdAt"] = FieldValue.serverTimestamp()
            }
            tx.set(ref, data, SetOptions.merge())
        }.await()
    }
}
