package com.example.playlistmaker.feature_medialibrary.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.feature_search.domain.model.Track
import com.example.playlistmaker.feature_search.presentation.SearchAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.R

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesViewModel by viewModel()

    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    private fun setupAdapter() {
        adapter = SearchAdapter(emptyList()) { track ->
            navigateToPlayer(track)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }

    private fun updateUI(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> {
                showEmptyState()
            }
            is FavoritesState.Content -> {
                showFavorites(state.tracks)
            }
        }
    }

    private fun showEmptyState() {
        binding.recyclerView.isVisible = false
        binding.imagePlaceholder.isVisible = true
        binding.textPlaceholder.isVisible = true
    }

    private fun showFavorites(tracks: List<Track>) {
        binding.recyclerView.isVisible = true
        binding.imagePlaceholder.isVisible = false
        binding.textPlaceholder.isVisible = false
        adapter.updateTracks(tracks)
    }

    private fun navigateToPlayer(track: Track) {
        try {

            val action = FavoritesFragmentDirections.actionFavoritesFragmentToPlayerFragment(track)
            findNavController().navigate(action)
        } catch (e: Exception) {
            e.printStackTrace()

            try {
                val bundle = Bundle().apply {
                    putParcelable("track", track)
                }
                findNavController().navigate(R.id.playerFragment, bundle)
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}