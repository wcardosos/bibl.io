package com.example.biblio.ui.library

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.TextView
import com.example.biblio.R
import com.example.biblio.model.Book
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
    private val status: TextView
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
        status.text      = book.status
        rating.rating    = book.rating
        cover.load(book.coverUrl) {
            placeholder(R.drawable.ic_book_24)
            error(R.drawable.ic_book_24)
        }
    }
}