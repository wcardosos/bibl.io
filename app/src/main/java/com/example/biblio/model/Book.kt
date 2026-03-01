package com.example.biblio.model

data class Book(
    val title: String,
    val author: String,
    val pages: Int,
    val status: String,
    val rating: Float,
    val coverUrl: String
)