package com.example.biblio.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.biblio.model.Book
import com.example.biblio.ui.library.BookCardView

class BookAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val card: BookCardView) : RecyclerView.ViewHolder(card) {
        fun bind(book: Book) {
            card.bind(book)
            card.setOnClickListener { onBookClick(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val card = BookCardView(parent.context)
        card.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return BookViewHolder(card)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size
}