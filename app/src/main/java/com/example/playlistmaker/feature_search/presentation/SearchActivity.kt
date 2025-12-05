package com.example.playlistmaker.feature_search.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.feature_player.presentation.PlayerActivity
import com.example.playlistmaker.feature_search.di.SearchComponent
import com.example.playlistmaker.feature_search.domain.model.Track

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels {
        SearchComponent.createViewModelFactory(this)
    }

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter

    private var lastClickTime = 0L
    private val CLICK_DEBOUNCE_DELAY = 1000L

    // ActivityResultLauncher для отслеживания возврата из плеера
    private lateinit var playerLauncher: ActivityResultLauncher<Intent>

    // Флаг для предотвращения бесконечного цикла
    private var isTextChangeFromUser = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Регистрируем ActivityResultLauncher
        playerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // При возврате из плеера просто обновляем историю
            viewModel.loadSearchHistory()
        }

        setupEdgeToEdge()
        setupAdapters()
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // При возвращении на экран просто обновляем историю
        viewModel.loadSearchHistory()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navigationBarInsets.bottom
            )

            insets
        }
    }

    private fun setupAdapters() {
        searchAdapter = SearchAdapter(emptyList()) { track: Track ->
            onTrackClick(track)
        }

        historyAdapter = SearchAdapter(emptyList()) { track: Track ->
            onHistoryTrackClick(track)
        }

        binding.searchResultsRecycler.layoutManager = LinearLayoutManager(this)
        binding.searchResultsRecycler.adapter = searchAdapter

        binding.historyRecycler.layoutManager = LinearLayoutManager(this)
        binding.historyRecycler.adapter = historyAdapter
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.isVisible = s?.isNotEmpty() == true
            }

            override fun afterTextChanged(s: Editable?) {
                if (isTextChangeFromUser) {
                    val query = s?.toString()?.trim() ?: ""
                    viewModel.search(query)
                }
            }
        })

        binding.clearButton.setOnClickListener {
            isTextChangeFromUser = false
            binding.searchEditText.setText("")
            isTextChangeFromUser = true
            hideKeyboard()
            viewModel.showHistory()
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.retryButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.search(query)
            }
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            updateUI(state)
        }

        viewModel.event.observe(this) { event ->
            event?.let {
                it.errorMessage?.let { message ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }

                it.navigateToPlayer?.let { track ->
                    val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    intent.putExtra("track", track)
                    playerLauncher.launch(intent)
                }
            }
        }
    }

    private fun updateUI(state: SearchState) {
        binding.progressBar.isVisible = state.isLoading

        // История показывается только если есть элементы И мы не в процессе поиска
        val hasHistory = state.history.isNotEmpty()
        val showHistory = state.isHistoryVisible && hasHistory && !state.isSearching

        // Показываем историю
        if (showHistory) {
            binding.historyLayout.isVisible = true
            binding.historyTitle.isVisible = true
            binding.clearHistoryButton.isVisible = true
            historyAdapter.updateTracks(state.history)
        } else {
            binding.historyLayout.isVisible = false
            binding.historyTitle.isVisible = false
            binding.clearHistoryButton.isVisible = false
        }

        // Показываем результаты поиска
        val showSearchResults = state.isSearching && !state.isLoading && !state.isNoResults
        binding.searchResultsRecycler.isVisible = showSearchResults

        if (showSearchResults) {
            searchAdapter.updateTracks(state.tracks)
        }

        // Ошибка
        binding.errorLayout.isVisible = state.isError

        // Нет результатов
        binding.noResultsLayout.isVisible = state.isNoResults

        // Прогресс
        binding.progressBar.isVisible = state.isLoading
    }

    private fun onTrackClick(track: Track) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime

            // Добавляем в историю и открываем плеер через ViewModel
            viewModel.navigateToPlayer(track)
        }
    }

    private fun onHistoryTrackClick(track: Track) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime

            // Добавляем в историю и открываем плеер через ViewModel
            viewModel.navigateToPlayer(track)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}