package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var noResultsTextView: TextView
    private var searchQuery: String = ""

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerView()
        setupToolbar()
        setupSearchEditText()
        restoreState(savedInstanceState)
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.search_results_recycler)
        noResultsTextView = findViewById(R.id.no_results_text)
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
                    performSearch()
                }
            })

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.isNotEmpty()) {
                    clearButton.isVisible = true
                }
            }
        }
    }

    private fun performSearch() {
        val filteredTracks = if (searchQuery.isEmpty()) {
            emptyList()
        } else {
            getMockTracks().filter {
                it.trackName.contains(searchQuery, ignoreCase = true) ||
                        it.artistName.contains(searchQuery, ignoreCase = true)
            }
        }

        updateSearchResults(filteredTracks)
    }

    private fun updateSearchResults(tracks: List<Track>) {
        adapter.updateTracks(tracks)

        if (tracks.isEmpty() && searchQuery.isNotEmpty()) {
            recyclerView.visibility = View.GONE
            noResultsTextView.visibility = View.VISIBLE
            noResultsTextView.text = getString(R.string.no_results, searchQuery)
        } else {
            recyclerView.visibility = if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
            noResultsTextView.visibility = View.GONE
        }
    }

    private fun getMockTracks(): List<Track> {
        return listOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
            )
        )
    }

    private fun clearSearch() {
        searchEditText.apply {
            text.clear()
            clearFocus()
            hideKeyboard()
        }
        clearButton.isVisible = false
        searchQuery = ""
        updateSearchResults(emptyList())
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