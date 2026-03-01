package com.example.biblio.adapters

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.biblio.ui.library.AllBooksTabFragment
import com.example.biblio.ui.library.ReadBooksTabFragment
import com.example.biblio.ui.library.ReadingBooksTabFragment
import com.example.biblio.ui.library.WishedBooksTabFragment

class LibraryAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val tabs = listOf(
        "Todos"    to AllBooksTabFragment(),
        "Quero ler" to WishedBooksTabFragment(),
        "Lendo"    to ReadingBooksTabFragment(),
        "Lidos"    to ReadBooksTabFragment()
    )

    override fun getItemCount() = tabs.size

    override fun createFragment(position: Int) = tabs[position].second

    fun getTabTitle(position: Int) = tabs[position].first
}