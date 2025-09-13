package com.example.playlistmaker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorLayout: View
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var noResultsLayout: LinearLayout
    private lateinit var noResultsImage: ImageView
    private lateinit var noResultsText: TextView

    // Новые переменные для истории поиска
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyTitle: TextView
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyAdapter: TrackAdapter

    private var searchQuery: String = ""
    private var searchJob: android.os.Handler? = null
    private val searchRunnable = Runnable { performSearch() }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
        private const val SEARCH_DELAY_MS = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация истории поиска
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)

        setupEdgeToEdge()
        initViews()
        setupRecyclerView()
        setupHistoryRecyclerView()
        setupClearHistoryButton()
        setupToolbar()
        setupSearchEditText()
        restoreState(savedInstanceState)
        showHistoryIfNeeded()
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = statusBarInsets.top
            }
            insets
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.search_results_recycler)
        progressBar = findViewById(R.id.progressBar)
        errorLayout = findViewById(R.id.errorLayout)
        errorImage = findViewById(R.id.errorImage)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)
        noResultsLayout = findViewById(R.id.noResultsLayout)
        noResultsImage = findViewById(R.id.noResultsImage)
        noResultsText = findViewById(R.id.noResultsText)

        // Новые view для истории
        historyLayout = findViewById(R.id.historyLayout)
        historyRecyclerView = findViewById(R.id.history_recycler)
        clearHistoryButton = findViewById(R.id.clear_history_button)
        historyTitle = findViewById(R.id.history_title)
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList()) { track ->
            // Добавляем трек в историю при клике
            searchHistory.addTrack(track)
            Toast.makeText(this, "Выбран: ${track.trackName}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = TrackAdapter(emptyList()) { track ->
            // При клике на трек из истории добавляем его снова (перемещаем вверх)
            searchHistory.addTrack(track)
            searchEditText.setText(track.trackName)
            performSearch()
        }

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupClearHistoryButton() {
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            hideHistory()
        }
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSearchEditText() {
        searchEditText.apply {
            hint = getString(R.string.search_hint)
            maxLines = 1
            inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER
            imeOptions = EditorInfo.IME_ACTION_DONE

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard()
                    performSearch()
                    true
                } else {
                    false
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    clearButton.isVisible = !s.isNullOrEmpty()
                    searchQuery = s?.toString() ?: ""

                    // Показываем/скрываем историю в зависимости от текста
                    if (searchQuery.isEmpty()) {
                        showHistoryIfNeeded()
                    } else {
                        hideHistory()
                    }

                    searchJob?.removeCallbacks(searchRunnable)

                    if (searchQuery.isNotEmpty()) {
                        searchJob = android.os.Handler(android.os.Looper.getMainLooper()).apply {
                            postDelayed(searchRunnable, SEARCH_DELAY_MS)
                        }
                    } else {
                        showEmptyState()
                    }
                }
            })

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.isNotEmpty()) {
                    clearButton.isVisible = true
                }
                if (hasFocus && text.isEmpty()) {
                    showHistoryIfNeeded()
                }
            }
        }

        clearButton.setOnClickListener {
            clearSearch()
        }

        retryButton.setOnClickListener {
            performSearch()
        }
    }

    private fun showHistoryIfNeeded() {
        if (searchHistory.hasHistory() && searchQuery.isEmpty()) {
            val historyTracks = searchHistory.getHistory()
            historyAdapter.updateTracks(historyTracks)
            historyLayout.visibility = View.VISIBLE
            hideAllSearchStates()
        } else {
            hideHistory()
        }
    }

    private fun hideHistory() {
        historyLayout.visibility = View.GONE
    }

    private fun hideAllSearchStates() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE
        noResultsLayout.visibility = View.GONE
    }

    private fun performSearch() {
        if (searchQuery.isEmpty()) {
            showHistoryIfNeeded()
            return
        }

        showLoading()
        hideHistory()

        Thread {
            try {
                val call = RetrofitClient.api.searchTracks(searchQuery)
                val response = call.execute()
                runOnUiThread {
                    if (response.isSuccessful && response.body() != null) {
                        val trackResponse = response.body()!!
                        if (trackResponse.resultCount > 0) {
                            showSearchResults(trackResponse.results)
                        } else {
                            showNoResults()
                        }
                    } else {
                        showErrorState()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showErrorState()
                }
            }
        }.start()
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        noResultsLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showSearchResults(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        noResultsLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        adapter.updateTracks(tracks)
        recyclerView.visibility = View.VISIBLE
    }

    private fun showNoResults() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE
        historyLayout.visibility = View.GONE
        noResultsLayout.visibility = View.VISIBLE
        noResultsText.text = getString(R.string.no_results_found)

        val noResultsDrawable = if (isDarkTheme()) {
            R.drawable.noinfdark
        } else {
            R.drawable.noinflight
        }
        noResultsImage.setImageResource(noResultsDrawable)
    }

    private fun showErrorState() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        noResultsLayout.visibility = View.GONE
        historyLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
        errorText.text = getString(R.string.connection_error)

        val errorDrawable = if (isDarkTheme()) {
            R.drawable.nointernetdark
        } else {
            R.drawable.nointernetlight
        }
        errorImage.setImageResource(errorDrawable)
    }

    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE
        noResultsLayout.visibility = View.GONE
        showHistoryIfNeeded()
    }

    private fun isDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun clearSearch() {
        searchJob?.removeCallbacks(searchRunnable)
        searchEditText.apply {
            text.clear()
            clearFocus()
            hideKeyboard()
        }
        clearButton.isVisible = false
        searchQuery = ""
        showHistoryIfNeeded()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            searchEditText.setText(searchQuery)
            if (searchQuery.isNotEmpty()) {
                performSearch()
            } else {
                showHistoryIfNeeded()
            }
        } else {
            showHistoryIfNeeded()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchJob?.removeCallbacks(searchRunnable)
    }
}