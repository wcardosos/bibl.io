package com.example.biblio.ui.library

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.TextView
import com.example.biblio.R
import com.example.biblio.model.Book
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import com.google.android.material.imageview.ShapeableImageView
import coil.load

class BookCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val cover: ShapeableImageView
    private val title: TextView
    private val authorPages: TextView
    private val status: Chip
    private val rating: RatingBar

    init {
        inflate(context, R.layout.view_book_card, this)

        cover       = findViewById(R.id.book_cover)
        title       = findViewById(R.id.book_title)
        authorPages = findViewById(R.id.book_author_pages)
        status      = findViewById(R.id.book_status)
        rating      = findViewById(R.id.book_rating)
    }

    fun bind(book: Book) {
        title.text       = book.title
        authorPages.text = "${book.author} - ${book.pages} páginas"
        rating.rating    = (book.rating ?: 0).toFloat()

        val (label, bgAttr, textAttr) = when (book.status) {
            "READ"    -> Triple("Lido",       com.google.android.material.R.attr.colorTertiaryContainer,  com.google.android.material.R.attr.colorOnTertiaryContainer)
            "READING" -> Triple("Lendo",      com.google.android.material.R.attr.colorSecondaryContainer, com.google.android.material.R.attr.colorOnSecondaryContainer)
            "WISH"    -> Triple("Quero ler",  com.google.android.material.R.attr.colorPrimaryContainer,   com.google.android.material.R.attr.colorOnPrimaryContainer)
            else      -> Triple(book.status,  com.google.android.material.R.attr.colorSurfaceVariant,     com.google.android.material.R.attr.colorOnSurfaceVariant)
        }
        status.text = label
        status.chipBackgroundColor = ColorStateList.valueOf(MaterialColors.getColor(context, bgAttr, 0))
        status.setTextColor(MaterialColors.getColor(context, textAttr, 0))
        cover.load(book.coverUrl) {
            placeholder(R.drawable.ic_book_24)
            error(R.drawable.ic_book_24)
        }
    }
}