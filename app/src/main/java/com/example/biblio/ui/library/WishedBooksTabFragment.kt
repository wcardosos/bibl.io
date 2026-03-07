package com.example.biblio.ui.library

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.biblio.R
import com.example.biblio.adapters.BookAdapter

class WishedBooksTabFragment : Fragment(R.layout.fragment_wished_books_tab) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val emptyState = view.findViewById<TextView>(R.id.empty_state)
        val adapter = BookAdapter(emptyList()) { }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val viewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]
        viewModel.books.observe(viewLifecycleOwner) { books ->
            val filtered = books.filter { it.status == "WISH" }
            adapter.updateBooks(filtered)
            recyclerView.isVisible = filtered.isNotEmpty()
            emptyState.isVisible = filtered.isEmpty()
        }
    }
}
