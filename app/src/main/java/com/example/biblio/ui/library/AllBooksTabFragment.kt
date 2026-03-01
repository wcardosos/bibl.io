package com.example.biblio.ui.library

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.biblio.R
import com.example.biblio.adapters.BookAdapter
import com.example.biblio.model.Book

class AllBooksTabFragment : Fragment(R.layout.fragment_all_books_tab) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val books = listOf(
            Book("Laranja Mecânica", "Anthony Burgess", 224, "Lido", 4.5f, "https://..."),
            Book("Crime e Castigo", "Dostoiévski", 551, "Lido", 5.0f, "https://..."),
            Book("O Idiota", "Dostoiévski", 682, "Lido", 4.0f, "https://...")
        )

        recyclerView.adapter = BookAdapter(books) { book ->
            // ação ao clicar no card
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}