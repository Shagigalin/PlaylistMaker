package com.example.playlistmaker.feature_medialibrary.presentation.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistViewModel by viewModel()

    companion object {
        fun newInstance() = PlaylistFragment()
    }

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

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.buttonCreatePlaylist.setOnClickListener {
            // TODO: Реализовать создание плейлиста в будущем
        }
    }

    private fun observeViewModel() {
        // Пока ничего не наблюдается, но структура готова
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}