package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    private val adapter = TrackAdapter(emptyList()) { track ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            searchHistory.addTrack(track)
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

    private lateinit var searchHistory: SearchHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupEdgeToEdge()
        initViews()
        setupToolbar()
        setupSearchHistory()
        setupSearchField()
        setupErrorHandling()
        showHistory()

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
            searchHistory.clearHistory()
            showHistory()
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {

            finish()
        }
    }

    private fun setupSearchHistory() {
        val sharedPreferences = getSharedPreferences("search_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
    }

    private fun setupSearchField() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = s?.isNotEmpty() == true
            }

            override fun afterTextChanged(s: Editable?) {
                // Отменяем предыдущий поиск
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                if (s.isNullOrEmpty()) {
                    showHistory()
                } else {
                    hideHistory()

                    // Создаем новый поиск с debounce
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

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/") // Прямо указываем URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val iTunesService = retrofit.create(iTunesApi::class.java)

        // Выполняем поиск
        iTunesService.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                hideLoading()
                if (response.isSuccessful && response.body() != null) {
                    val tracks = response.body()!!.results
                    if (tracks.isNotEmpty()) {
                        showTracks(tracks)
                    } else {
                        showNoResults()
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                hideLoading()
                showError()
            }
        })
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
        val history = searchHistory.getHistory()
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