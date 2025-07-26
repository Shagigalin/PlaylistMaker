package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)

        // Восстановление состояния при создании
        if (savedInstanceState != null) {
            searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
            searchEditText.setText(searchQuery)
        }

        // Настройка поля поиска
        setupSearchEditText()

        // Обработчик кнопки очистки
        clearButton.setOnClickListener { clearSearch() }

        // Обработчик кнопки назад
        findViewById<ImageView>(R.id.back_button).setOnClickListener { finish() }
    }

    private fun setupSearchEditText() {
        searchEditText.apply {
            hint = getString(R.string.search_hint)
            maxLines = 1
            inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER
            imeOptions = EditorInfo.IME_ACTION_DONE

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    toggleClearButton(s?.isNotEmpty() == true)
                }
            })

            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && text.isNotEmpty()) {
                    toggleClearButton(true)
                }
            }
        }
    }

    private fun toggleClearButton(shouldShow: Boolean) {
        clearButton.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    private fun clearSearch() {
        searchEditText.apply {
            text.clear()
            clearFocus()
            toggleClearButton(false)
            hideKeyboard()
            searchQuery = ""
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, searchQuery)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        searchEditText.setText(searchQuery)
    }

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }
}