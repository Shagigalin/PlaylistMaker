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

    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    override fun onResume() {
        super.onResume()
        viewModel.restorePreviousSearch()
        viewModel.loadSearchHistory()
    }

    private fun setupAdapters() {
        searchAdapter = SearchAdapter(emptyList()) { track: Track ->

            viewModel.onTrackClick(track)
        }

        historyAdapter = SearchAdapter(emptyList()) { track: Track ->

            viewModel.onTrackClick(track)
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

                viewModel.performSearchDirectly(query)
            } else {

                viewModel.showHistory()
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
        if (state.isError) {

            val errorImage = when (isDarkTheme()) {
                true -> R.drawable.nointernetdark
                false -> R.drawable.nointernetlight
            }
            binding.errorImage.setImageResource(errorImage)
            binding.errorText.text = getString(R.string.connection_error)
        }
        binding.noResultsLayout.isVisible = state.isNoResults

        if (state.isNoResults) {
            val noResultsImage = when (isDarkTheme()) {
                true -> R.drawable.noinfdark
                false -> R.drawable.noinflight
            }
            binding.noResultsImage.setImageResource(noResultsImage)
            binding.noResultsText.text = getString(R.string.no_results_found)
        }

        binding.progressBar.isVisible = state.isLoading
    }

    private fun isDarkTheme(): Boolean {
        val configuration = resources.configuration
        val nightModeFlags = configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
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