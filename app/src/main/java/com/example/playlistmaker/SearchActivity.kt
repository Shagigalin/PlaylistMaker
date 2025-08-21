package com.example.playlistmaker

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
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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

    private var searchQuery: String = ""
    private var currentSearchJob: Job? = null

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
        private const val SEARCH_DELAY_MS = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupEdgeToEdge()
        initViews()
        setupRecyclerView()
        setupToolbar()
        setupSearchEditText()
        restoreState(savedInstanceState)
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
    }

    private fun setupRecyclerView() {
        adapter = TrackAdapter(emptyList()) { track ->
            Toast.makeText(this, "Выбран: ${track.trackName}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
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

                    currentSearchJob?.cancel()

                    if (searchQuery.isNotEmpty()) {
                        currentSearchJob = CoroutineScope(Dispatchers.Main).launch {
                            delay(SEARCH_DELAY_MS)
                            performSearch()
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
            }
        }

        clearButton.setOnClickListener {
            clearSearch()
        }

        retryButton.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        if (searchQuery.isEmpty()) {
            showEmptyState()
            return
        }

        showLoading()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.searchTracks(searchQuery)
                withContext(Dispatchers.Main) {
                    if (response.resultCount > 0) {
                        showSearchResults(response.results)
                    } else {
                        showNoResults()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorState()
                }
            }
        }
    }

    private fun showLoading() {
        recyclerView.visibility = View.GONE
        noResultsLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showSearchResults(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        noResultsLayout.visibility = View.GONE

        adapter.updateTracks(tracks)
        recyclerView.visibility = View.VISIBLE
    }

    private fun showNoResults() {
        progressBar.visibility = View.GONE
        errorLayout.visibility = View.GONE
        recyclerView.visibility = View.GONE

        noResultsLayout.visibility = View.VISIBLE
        noResultsText.text = getString(R.string.no_results_found) // Исправлено

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
    }

    private fun isDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) {
            android.content.res.Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun clearSearch() {
        searchEditText.apply {
            text.clear()
            clearFocus()
            hideKeyboard()
        }
        clearButton.isVisible = false
        searchQuery = ""
        showEmptyState()
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
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }
}

