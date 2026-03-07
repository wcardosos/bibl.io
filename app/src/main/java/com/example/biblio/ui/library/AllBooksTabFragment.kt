package com.example.biblio.ui.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblio.R
import com.example.biblio.adapters.BookAdapter

class AllBooksTabFragment : Fragment(R.layout.fragment_all_books_tab) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val adapter = BookAdapter(emptyList()) { book ->
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.detail_container, BookDetailFragment.newInstance(book))
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val viewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]
        viewModel.books.observe(viewLifecycleOwner) { books ->
            adapter.updateBooks(books)
        }
    }
}
