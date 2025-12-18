package com.example.playlistmaker.feature_search.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.feature_search.domain.model.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyAdapter: SearchAdapter

    private var lastClickTime = 0L
    private val CLICK_DEBOUNCE_DELAY = 1000L
    private var isTextChangeFromUser = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupViews()
        setupObservers()

    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSearchHistory()
    }

    private fun setupAdapters() {
        searchAdapter = SearchAdapter(emptyList()) { track: Track ->
            onTrackClick(track)
        }

        historyAdapter = SearchAdapter(emptyList()) { track: Track ->
            onHistoryTrackClick(track)
        }

        binding.searchResultsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResultsRecycler.adapter = searchAdapter

        binding.historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecycler.adapter = historyAdapter
    }

    private fun setupViews() {
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
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let {
                it.errorMessage?.let { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }

                it.navigateToPlayer?.let { track ->
                    // Навигация к PlayerFragment через Navigation Component
                    val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun updateUI(state: SearchState) {
        binding.progressBar.isVisible = state.isLoading

        val hasHistory = state.history.isNotEmpty()
        val showHistory = state.isHistoryVisible && hasHistory && !state.isSearching

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

        val showSearchResults = state.isSearching && !state.isLoading && !state.isNoResults
        binding.searchResultsRecycler.isVisible = showSearchResults

        if (showSearchResults) {
            searchAdapter.updateTracks(state.tracks)
        }

        binding.errorLayout.isVisible = state.isError
        binding.noResultsLayout.isVisible = state.isNoResults
        binding.progressBar.isVisible = state.isLoading
    }

    private fun onTrackClick(track: Track) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            viewModel.navigateToPlayer(track)
        }
    }

    private fun onHistoryTrackClick(track: Track) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > CLICK_DEBOUNCE_DELAY) {
            lastClickTime = currentTime
            viewModel.navigateToPlayer(track)
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}