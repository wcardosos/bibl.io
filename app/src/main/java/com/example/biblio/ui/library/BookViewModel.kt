package com.example.biblio.ui.library

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.biblio.model.Book
import com.example.biblio.repository.BookRepository
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {

    sealed class AddBookState {
        object Idle : AddBookState()
        object Loading : AddBookState()
        object Success : AddBookState()
        data class Error(val message: String) : AddBookState()
    }

    private val repository = BookRepository(application)

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
}
