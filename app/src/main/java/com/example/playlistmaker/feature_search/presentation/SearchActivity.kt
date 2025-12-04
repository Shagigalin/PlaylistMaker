package com.example.playlistmaker.feature_search.presentation

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEdgeToEdge()
        setupAdapters()
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        val query = binding.searchEditText.text.toString()
        if (query.isEmpty()) {
                       viewModel.showHistory()
        }
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
            onTrackClick(track)
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
                val query = s?.toString()?.trim() ?: ""

                viewModel.search(query)
            }
        })

        binding.clearButton.setOnClickListener {
            binding.searchEditText.setText("")
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
                    startActivity(intent)
                }
            }
        }
    }

    private fun updateUI(state: SearchState) {

        binding.progressBar.isVisible = state.isLoading


        binding.historyLayout.isVisible = state.isHistoryVisible
        binding.clearHistoryButton.isVisible = state.isHistoryVisible && state.history.isNotEmpty()


        binding.searchResultsRecycler.isVisible =
            state.isSearching && !state.isLoading && !state.isNoResults


        binding.errorLayout.isVisible = state.isError


        binding.noResultsLayout.isVisible = state.isNoResults


        if (state.isSearching) {
            searchAdapter.updateTracks(state.tracks)
        }

        if (state.isHistoryVisible) {
            historyAdapter.updateTracks(state.history)
        }


    }

    private fun onTrackClick(track: Track) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime

            // Добавляем в историю
            viewModel.addToHistory(track)

            // Открываем плеер
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
    }



    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}