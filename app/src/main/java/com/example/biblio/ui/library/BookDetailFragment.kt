package com.example.biblio.ui.library

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.biblio.R
import com.example.biblio.model.Book
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import coil.load

class BookDetailFragment : Fragment(R.layout.fragment_book_detail) {

    companion object {
        private const val ARG_BOOK = "book"

        fun newInstance(book: Book) = BookDetailFragment().apply {
            arguments = Bundle().apply { putParcelable(ARG_BOOK, book) }
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[BookViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        val book = arguments?.getParcelable<Book>(ARG_BOOK) ?: return

        view.findViewById<MaterialToolbar>(R.id.toolbar_detail).apply {
            setNavigationOnClickListener { parentFragmentManager.popBackStack() }
            inflateMenu(R.menu.menu_book_detail)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.detail_container, AddBookFragment.newInstance(book))
                            .addToBackStack(null)
                            .commit()
                        true
                    }
                    R.id.action_delete -> {
                        showDeleteConfirmationDialog(book)
                        true
                    }
                    else -> false
                }
            }
        }

        val cover = view.findViewById<ShapeableImageView>(R.id.iv_cover)
        if (book.coverUrl.isNotEmpty()) {
            cover.load(book.coverUrl) {
                placeholder(R.drawable.ic_book_24)
                error(R.drawable.ic_book_24)
            }
        }

        view.findViewById<TextView>(R.id.tv_title).text = book.title
        view.findViewById<TextView>(R.id.tv_author).text = book.author

        val chip = view.findViewById<Chip>(R.id.chip_status)
        val (label, bgAttr, textAttr) = when (book.status) {
            "READ"    -> Triple("Lido",      com.google.android.material.R.attr.colorTertiaryContainer,  com.google.android.material.R.attr.colorOnTertiaryContainer)
            "READING" -> Triple("Lendo",     com.google.android.material.R.attr.colorSecondaryContainer, com.google.android.material.R.attr.colorOnSecondaryContainer)
            "WISH"    -> Triple("Quero ler", com.google.android.material.R.attr.colorPrimaryContainer,   com.google.android.material.R.attr.colorOnPrimaryContainer)
            else      -> Triple(book.status, com.google.android.material.R.attr.colorSurfaceVariant,     com.google.android.material.R.attr.colorOnSurfaceVariant)
        }
        chip.text = label
        chip.chipBackgroundColor = ColorStateList.valueOf(MaterialColors.getColor(requireContext(), bgAttr, 0))
        chip.setTextColor(MaterialColors.getColor(requireContext(), textAttr, 0))

        view.findViewById<RatingBar>(R.id.rating_bar).rating = (book.rating ?: 0).toFloat()

        view.findViewById<TextView>(R.id.tv_pages_value).text =
            if (book.pages > 0) book.pages.toString() else "—"
        view.findViewById<TextView>(R.id.tv_date_value).text = book.completionDate ?: "—"
        view.findViewById<TextView>(R.id.tv_review).text = book.review ?: "—"

        observeDeleteState(view)
    }

    private fun showDeleteConfirmationDialog(book: Book) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_book_title))
            .setMessage(getString(R.string.delete_book_message))
            .setNegativeButton(getString(R.string.delete_book_cancel), null)
            .setPositiveButton(getString(R.string.delete_book_confirm)) { _, _ ->
                viewModel.deleteBook(book)
            }
            .show()
    }

    private fun observeDeleteState(view: View) {
        viewModel.deleteBookState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookViewModel.AddBookState.Success -> {
                    viewModel.resetDeleteBookState()
                    parentFragmentManager.popBackStack()
                }
                is BookViewModel.AddBookState.Error -> {
                    Snackbar.make(view, getString(R.string.delete_book_error), Snackbar.LENGTH_LONG).show()
                    viewModel.resetDeleteBookState()
                }
                else -> {}
            }
        }
    }
}
