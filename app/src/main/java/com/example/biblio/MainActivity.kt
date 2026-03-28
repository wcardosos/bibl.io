package com.example.biblio

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.example.biblio.adapters.LibraryAdapter
import com.example.biblio.ui.library.AddBookFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // RF-003: Auth guard — nenhuma tela acessível sem autenticação válida
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        // RF-004: Avatar circular do usuário na toolbar
        toolbar.inflateMenu(R.menu.menu_main_toolbar)
        val avatarView = toolbar.menu.findItem(R.id.action_avatar)
            .actionView!!.findViewById<ShapeableImageView>(R.id.iv_avatar)
        val user = FirebaseAuth.getInstance().currentUser!!
        avatarView.load(user.photoUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_account_circle_24)
            error(R.drawable.ic_account_circle_24)
            transformations(CircleCropTransformation())
        }

        // RF-005: Menu de contexto com logout ao clicar no avatar
        avatarView.setOnClickListener { view ->
            PopupMenu(this, view).apply {
                inflate(R.menu.menu_user_avatar)
                setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.action_logout) {
                        performLogout()
                        true
                    } else {
                        false
                    }
                }
            }.show()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        val adapter = LibraryAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        val fab = findViewById<FloatingActionButton>(R.id.fab_add_book)

        fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_container, AddBookFragment())
                .addToBackStack(null)
                .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                fab.hide()
            } else {
                fab.show()
            }
        }
    }

    // RF-006: Logout — encerra sessão Firebase e Google, limpa cache, redireciona para login
    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener {
            // Limpa cache do app (SharedPreferences, cache do Firestore, dados em memória)
            // Imagens em filesDir/covers são mantidas intencionalmente (RF-006)
            cacheDir.deleteRecursively()

            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }
}
