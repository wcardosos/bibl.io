package com.example.biblio.ui.library

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.biblio.model.Book
import com.example.biblio.repository.BookRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {

    sealed class AddBookState {
        object Idle : AddBookState()
        object Loading : AddBookState()
        object Success : AddBookState()
        data class Error(val message: String) : AddBookState()
    }

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val repository = BookRepository(application, uid)

    val books: LiveData<List<Book>> = repository.getBooksLiveData()

    private val _addBookState = MutableLiveData<AddBookState>(AddBookState.Idle)
    val addBookState: LiveData<AddBookState> = _addBookState

    fun updateBook(book: Book, coverUri: Uri?) {
        _addBookState.value = AddBookState.Loading
        viewModelScope.launch {
            val result = repository.updateBook(book, coverUri)
            _addBookState.value = result.fold(
                onSuccess = { AddBookState.Success },
                onFailure = { AddBookState.Error(it.message ?: "Erro desconhecido") }
            )
        }
    }

    fun addBook(book: Book, coverUri: Uri?) {
        _addBookState.value = AddBookState.Loading
        viewModelScope.launch {
            val result = repository.addBook(book, coverUri)
            _addBookState.value = result.fold(
                onSuccess = { AddBookState.Success },
                onFailure = { AddBookState.Error(it.message ?: "Erro desconhecido") }
            )
        }
    }

    fun resetAddBookState() {
        _addBookState.value = AddBookState.Idle
    }

    private val _deleteBookState = MutableLiveData<AddBookState>(AddBookState.Idle)
    val deleteBookState: LiveData<AddBookState> = _deleteBookState

    fun deleteBook(book: Book) {
        _deleteBookState.value = AddBookState.Loading
        viewModelScope.launch {
            val result = repository.deleteBook(book)
            _deleteBookState.value = result.fold(
                onSuccess = { AddBookState.Success },
                onFailure = { AddBookState.Error(it.message ?: "Erro desconhecido") }
            )
        }
    }

    fun resetDeleteBookState() {
        _deleteBookState.value = AddBookState.Idle
    }
}
