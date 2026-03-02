package com.example.biblio.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.biblio.model.Book
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class BookRepository {
    private val db = Firebase.firestore

    fun getBooksLiveData(): LiveData<List<Book>> {
        val liveData = MutableLiveData<List<Book>>()
        db.collection("books").addSnapshotListener { snapshot, _ ->
            val books = snapshot?.toObjects(Book::class.java) ?: emptyList()
            liveData.value = books
        }
        return liveData
    }
}
