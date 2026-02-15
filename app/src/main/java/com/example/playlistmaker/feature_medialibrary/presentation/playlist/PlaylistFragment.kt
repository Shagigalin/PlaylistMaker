package com.example.playlistmaker.feature_medialibrary.presentation.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.presentation.adapter.PlaylistAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPlaylists()
    }

    private fun setupViews() {
        binding.buttonCreatePlaylist.setOnClickListener {
            findNavController().navigate(
                R.id.action_mediaLibraryFragment_to_createPlaylistFragment
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = PlaylistAdapter { playlist ->
            Toast.makeText(
                requireContext(),
                "Плейлист: ${playlist.name}",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@PlaylistFragment.adapter


            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val spanCount = 2


                    if (position % spanCount == 0) {
                        outRect.left = 0
                        outRect.right = 8.dpToPx()
                    }

                    else {
                        outRect.left = 8.dpToPx()
                        outRect.right = 0
                    }


                    outRect.bottom = 16.dpToPx()
                }
            })
        }
    }


    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistState.Loading -> showLoading()
                is PlaylistState.Empty -> showEmptyState()
                is PlaylistState.Content -> showPlaylists(state.playlists)
                is PlaylistState.Error -> showError(state.message)
            }
        }
    }

    private fun showLoading() {
        binding.recyclerView.visibility = View.GONE
        binding.imagePlaceholder.visibility = View.GONE
        binding.textPlaceholder.visibility = View.GONE
        binding.buttonCreatePlaylist.visibility = View.VISIBLE
    }

    private fun showEmptyState() {
        binding.recyclerView.visibility = View.GONE
        binding.imagePlaceholder.visibility = View.VISIBLE
        binding.textPlaceholder.visibility = View.VISIBLE
        binding.buttonCreatePlaylist.visibility = View.VISIBLE
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.recyclerView.visibility = View.VISIBLE
        binding.imagePlaceholder.visibility = View.GONE
        binding.textPlaceholder.visibility = View.GONE
        binding.buttonCreatePlaylist.visibility = View.VISIBLE

        adapter.submitList(playlists)
    }

    private fun showError(message: String) {
        binding.recyclerView.visibility = View.GONE
        binding.imagePlaceholder.visibility = View.VISIBLE
        binding.textPlaceholder.visibility = View.VISIBLE
        binding.buttonCreatePlaylist.visibility = View.VISIBLE

        Toast.makeText(requireContext(), "Ошибка: $message", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}