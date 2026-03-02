package com.example.biblio.model

data class Book(
    val title: String = "",
    val author: String = "",
    val pages: Int = 0,
    val status: String = "",
    val rating: Int? = null,
    val coverUrl: String = ""
)