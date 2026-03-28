package com.example.biblio.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.biblio.model.Book
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class BookRepository(private val context: Context, private val uid: String) {
    private val db = Firebase.firestore

    private fun booksCollection() =
        db.collection("users").document(uid).collection("books")

    fun getBooksLiveData(): LiveData<List<Book>> {
        val liveData = MutableLiveData<List<Book>>()
        booksCollection().addSnapshotListener { snapshot, _ ->
            val books = snapshot?.toObjects(Book::class.java) ?: emptyList()
            liveData.value = books
        }
        return liveData
    }

    suspend fun updateBook(book: Book, coverUri: Uri?): Result<Unit> {
        return try {
            val coverUrl = if (coverUri != null) saveCoverLocally(coverUri) else book.coverUrl
            booksCollection().document(book.id).set(book.copy(coverUrl = coverUrl)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addBook(book: Book, coverUri: Uri?): Result<Unit> {
        return try {
            val coverUrl = if (coverUri != null) saveCoverLocally(coverUri) else ""
            val bookWithCover = book.copy(coverUrl = coverUrl)
            booksCollection().add(bookWithCover).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveCoverLocally(uri: Uri): String = withContext(Dispatchers.IO) {
        val coversDir = File(context.filesDir, "covers").also { it.mkdirs() }
        if (uri.authority == "${context.packageName}.fileprovider") {
            // Câmera: arquivo já foi escrito diretamente em coversDir pelo FileProvider
            File(coversDir, File(uri.path!!).name).absolutePath
        } else {
            // Galeria: copiar stream para coversDir
            val dest = File(coversDir, "${UUID.randomUUID()}.jpg")
            val stream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Não foi possível abrir a imagem selecionada")
            stream.use { input -> FileOutputStream(dest).use { input.copyTo(it) } }
            dest.absolutePath
        }
    }
}
