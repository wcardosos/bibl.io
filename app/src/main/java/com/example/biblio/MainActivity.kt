package com.example.biblio

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.biblio.adapters.LibraryAdapter
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private val TAG = "HOME_PAGE"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val searchBar = findViewById<SearchBar>(R.id.search_bar)
        val searchView = findViewById<SearchView>(R.id.search_view)

        searchView.setupWithSearchBar(searchBar)

        val adapter = LibraryAdapter(this)
        viewPager.adapter = adapter

        // Conecta TabLayout com ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        // Escuta o texto digitado
        /*searchView.editText.addTextChangedListener { text ->
            // filtre sua lista aqui enquanto o usuário digita
            Log.d(TAG, "Texto procurado ${text}")

            return text;
        }*/

        // Escuta quando o usuário confirma (pressiona Enter)
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = searchView.text.toString()
            searchBar.setText(query)
            searchView.hide()
            false
        }
    }
}