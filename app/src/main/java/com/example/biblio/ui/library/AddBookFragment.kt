package com.example.biblio.ui.library

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.biblio.R
import com.example.biblio.databinding.FragmentAddBookBinding
import com.example.biblio.model.Book
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File

class AddBookFragment : Fragment() {

    companion object {
        private const val ARG_BOOK = "book"

        fun newInstance(book: Book) = AddBookFragment().apply {
            arguments = Bundle().apply { putParcelable(ARG_BOOK, book) }
        }
    }

    private var _binding: FragmentAddBookBinding? = null
    private val binding get() = _binding!!

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[BookViewModel::class.java]
    }

    private var editingBook: Book? = null
    private var coverUri: Uri? = null
    private var pendingCameraUri: Uri? = null
    private var pendingCameraFile: File? = null

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coverUri = it
            binding.coverImage.load(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && pendingCameraUri != null) {
            coverUri = pendingCameraUri
            binding.coverImage.load(coverUri)
        } else {
            pendingCameraFile?.delete()
        }
        pendingCameraFile = null
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) launchCamera()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        editingBook = arguments?.getParcelable(ARG_BOOK)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            view.updatePadding(bottom = (imeBottom - navBar).coerceAtLeast(0))
            insets
        }

        setupToolbar()
        setupCoverPicker()
        setupStatusChips()
        setupValidation()
        setupSaveButton()
        observeViewModel()

        editingBook?.let { prefillForm(it) }
    }

    private fun prefillForm(book: Book) {
        binding.toolbar.title = "Editar livro"
        binding.editName.setText(book.title)
        binding.editAuthor.setText(book.author)
        binding.editPages.setText(if (book.pages > 0) book.pages.toString() else "")
        binding.editReview.setText(book.review ?: "")

        val chipId = when (book.status) {
            "WISH"    -> R.id.chip_wish
            "READING" -> R.id.chip_reading
            "READ"    -> R.id.chip_read
            else      -> View.NO_ID
        }
        if (chipId != View.NO_ID) binding.chipGroupStatus.check(chipId)

        if (book.status == "READ") {
            binding.ratingSection.visibility = View.VISIBLE
            binding.ratingBar.rating = (book.rating ?: 0).toFloat()
        }

        if (book.coverUrl.isNotEmpty()) {
            binding.coverImage.load(book.coverUrl) {
                placeholder(R.drawable.ic_book_24)
                error(R.drawable.ic_book_24)
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupCoverPicker() {
        binding.btnAddCover.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Adicionar capa")
                .setItems(arrayOf("Câmera", "Galeria")) { _, which ->
                    when (which) {
                        0 -> checkCameraPermissionAndLaunch()
                        1 -> galleryLauncher.launch("image/*")
                    }
                }
                .show()
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        val coversDir = File(requireContext().filesDir, "covers").also { it.mkdirs() }
        val imageFile = File(coversDir, "camera_${System.currentTimeMillis()}.jpg")
        pendingCameraFile = imageFile
        pendingCameraUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
        cameraLauncher.launch(pendingCameraUri!!)
    }

    private fun setupStatusChips() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            val isRead = checkedIds.contains(R.id.chip_read)
            binding.ratingSection.visibility = if (isRead) View.VISIBLE else View.GONE
            validateForm()
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateForm() }
        }
        binding.editName.addTextChangedListener(watcher)
        binding.editAuthor.addTextChangedListener(watcher)
        binding.editPages.addTextChangedListener(watcher)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.editName.text.toString().trim()
        if (name.isBlank()) {
            binding.inputName.error = "Campo obrigatório"
            valid = false
        } else {
            binding.inputName.error = null
        }

        val author = binding.editAuthor.text.toString().trim()
        if (author.isBlank()) {
            binding.inputAuthor.error = "Campo obrigatório"
            valid = false
        } else {
            binding.inputAuthor.error = null
        }

        val pages = binding.editPages.text.toString().trim().toIntOrNull()
        if (pages == null || pages <= 0) {
            binding.inputPages.error = "Informe um número válido"
            valid = false
        } else {
            binding.inputPages.error = null
        }

        val statusSelected = binding.chipGroupStatus.checkedChipId != View.NO_ID
        if (!statusSelected) {
            binding.statusError.visibility = View.VISIBLE
            valid = false
        } else {
            binding.statusError.visibility = View.GONE
        }

        binding.btnSave.isEnabled = valid
        return valid
    }

    private fun setupSaveButton() {
        binding.btnSave.isEnabled = false
        binding.btnSave.setOnClickListener {
            if (validateForm()) saveBook()
        }
    }

    private fun saveBook() {
        val status = when (binding.chipGroupStatus.checkedChipId) {
            R.id.chip_wish -> "WISH"
            R.id.chip_reading -> "READING"
            R.id.chip_read -> "READ"
            else -> return
        }
        val rating = if (status == "READ") {
            binding.ratingBar.rating.toInt().takeIf { it > 0 }
        } else null

        val existing = editingBook
        if (existing != null) {
            val updated = existing.copy(
                title = binding.editName.text.toString().trim(),
                author = binding.editAuthor.text.toString().trim(),
                pages = binding.editPages.text.toString().trim().toInt(),
                status = status,
                rating = rating,
                review = binding.editReview.text.toString().trim().ifBlank { null }
            )
            viewModel.updateBook(updated, coverUri)
        } else {
            val book = Book(
                title = binding.editName.text.toString().trim(),
                author = binding.editAuthor.text.toString().trim(),
                pages = binding.editPages.text.toString().trim().toInt(),
                status = status,
                rating = rating,
                review = binding.editReview.text.toString().trim().ifBlank { null }
            )
            viewModel.addBook(book, coverUri)
        }
    }

    private fun observeViewModel() {
        viewModel.addBookState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BookViewModel.AddBookState.Loading -> {
                    binding.progressIndicator.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = false
                }
                is BookViewModel.AddBookState.Success -> {
                    binding.progressIndicator.visibility = View.GONE
                    viewModel.resetAddBookState()
                    if (editingBook != null) {
                        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    } else {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
                is BookViewModel.AddBookState.Error -> {
                    binding.progressIndicator.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                        .setAction("Tentar novamente") { saveBook() }
                        .show()
                    viewModel.resetAddBookState()
                }
                is BookViewModel.AddBookState.Idle -> {
                    binding.progressIndicator.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
