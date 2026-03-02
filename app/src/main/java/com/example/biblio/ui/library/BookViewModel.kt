package com.example.biblio.ui.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.biblio.model.Book
import com.example.biblio.repository.BookRepository

class BookViewModel : ViewModel() {
    private val repository = BookRepository()
    val books: LiveData<List<Book>> = repository.getBooksLiveData()
}
