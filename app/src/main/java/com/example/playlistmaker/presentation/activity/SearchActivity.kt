package com.example.playlistmaker.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.usecase.AddToSearchHistoryUseCaseInterface
import com.example.playlistmaker.domain.usecase.ClearSearchHistoryUseCaseInterface
import com.example.playlistmaker.domain.usecase.GetSearchHistoryUseCaseInterface
import com.example.playlistmaker.domain.usecase.SearchTracksUseCaseInterface
import com.example.playlistmaker.presentation.adapter.TrackAdapter
import com.example.playlistmaker.data.di.Creator
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var searchResultsRecycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecycler: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var errorLayout: LinearLayout
    private lateinit var noResultsLayout: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var toolbar: MaterialToolbar

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val SEARCH_DEBOUNCE_DELAY = 2000L

    private var lastClickTime = 0L
    private val CLICK_DEBOUNCE_DELAY = 1000L

    // Use Cases
    private lateinit var searchTracksUseCase: SearchTracksUseCaseInterface
    private lateinit var getSearchHistoryUseCase: GetSearchHistoryUseCaseInterface
    private lateinit var addToSearchHistoryUseCase: AddToSearchHistoryUseCaseInterface
    private lateinit var clearSearchHistoryUseCase: ClearSearchHistoryUseCaseInterface

    private val adapter = TrackAdapter(emptyList()) { track ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            addToSearchHistoryUseCase.execute(track)
            val intent = Intent(this@SearchActivity, MediaActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
    }

    private val historyAdapter = TrackAdapter(emptyList()) { track ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            val intent = Intent(this@SearchActivity, MediaActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация Use Cases через Creator
        initUseCases()

        setupEdgeToEdge()
        initViews()
        setupToolbar()
        setupSearchField()
        setupErrorHandling()
        showHistory()
    }

    private fun initUseCases() {
        searchTracksUseCase = Creator.provideSearchTracksUseCase()
        getSearchHistoryUseCase = Creator.provideGetSearchHistoryUseCase(this)
        addToSearchHistoryUseCase = Creator.provideAddToSearchHistoryUseCase(this)
        clearSearchHistoryUseCase = Creator.provideClearSearchHistoryUseCase(this)
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        searchResultsRecycler = findViewById(R.id.search_results_recycler)
        progressBar = findViewById(R.id.progressBar)
        historyLayout = findViewById(R.id.historyLayout)
        historyRecycler = findViewById(R.id.history_recycler)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        errorLayout = findViewById(R.id.errorLayout)
        noResultsLayout = findViewById(R.id.noResultsLayout)
        retryButton = findViewById(R.id.retryButton)

        searchResultsRecycler.layoutManager = LinearLayoutManager(this)
        searchResultsRecycler.adapter = adapter

        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter

        clearHistoryButton.setOnClickListener {
            clearSearchHistoryUseCase.execute()
            showHistory()
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupSearchField() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = s?.isNotEmpty() == true
            }

            override fun afterTextChanged(s: Editable?) {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                if (s.isNullOrEmpty()) {
                    showHistory()
                } else {
                    hideHistory()

                    searchRunnable = Runnable {
                        performSearch(s.toString())
                    }
                    searchRunnable?.let {
                        searchHandler.postDelayed(it, SEARCH_DEBOUNCE_DELAY)
                    }
                }
            }
        })

        clearButton.setOnClickListener {
            searchEditText.setText("")
            hideKeyboard()
            showHistory()
        }
    }

    private fun setupErrorHandling() {
        retryButton.setOnClickListener {
            val query = searchEditText.text.toString()
            if (query.isNotEmpty()) {
                performSearch(query)
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) return

        showLoading()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val tracks = withContext(Dispatchers.IO) {
                    searchTracksUseCase.execute(query)
                }
                if (tracks.isNotEmpty()) {
                    showTracks(tracks)
                } else {
                    showNoResults()
                }
            } catch (e: Exception) {
                showError()
            }
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        searchResultsRecycler.isVisible = false
        historyLayout.isVisible = false
        errorLayout.isVisible = false
        noResultsLayout.isVisible = false
    }

    private fun hideLoading() {
        progressBar.isVisible = false
    }

    private fun showTracks(tracks: List<Track>) {
        searchResultsRecycler.isVisible = true
        historyLayout.isVisible = false
        errorLayout.isVisible = false
        noResultsLayout.isVisible = false
        adapter.updateTracks(tracks)
    }

    private fun showHistory() {
        val history = getSearchHistoryUseCase.execute()
        if (history.isNotEmpty()) {
            historyLayout.isVisible = true
            searchResultsRecycler.isVisible = false
            progressBar.isVisible = false
            errorLayout.isVisible = false
            noResultsLayout.isVisible = false
            historyAdapter.updateTracks(history)
        } else {
            showPlaceholder()
        }
    }

    private fun hideHistory() {
        historyLayout.isVisible = false
    }

    private fun showPlaceholder() {
        searchResultsRecycler.isVisible = false
        progressBar.isVisible = false
        historyLayout.isVisible = false
        errorLayout.isVisible = false
        noResultsLayout.isVisible = false
    }

    private fun showNoResults() {
        noResultsLayout.isVisible = true
        searchResultsRecycler.isVisible = false
        historyLayout.isVisible = false
        errorLayout.isVisible = false
    }

    private fun showError() {
        errorLayout.isVisible = true
        searchResultsRecycler.isVisible = false
        historyLayout.isVisible = false
        noResultsLayout.isVisible = false
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updatePadding(
                top = statusBarInsets.top,
                bottom = navigationBarInsets.bottom
            )

            insets
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchHandler.removeCallbacksAndMessages(null)
    }
}