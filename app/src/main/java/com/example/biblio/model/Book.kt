package com.example.biblio.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val title: String = "",
    val author: String = "",
    val pages: Int = 0,
    val status: String = "",
    val rating: Int? = null,
    val coverUrl: String = "",
    val synopsis: String? = null,
    val review: String? = null,
    val completionDate: String? = null
) : Parcelable
