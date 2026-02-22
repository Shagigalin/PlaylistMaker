package com.example.playlistmaker.feature_playlist.presentation.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.feature_playlist.presentation.create.CreatePlaylistFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : CreatePlaylistFragment() {

    private val args: EditPlaylistFragmentArgs by navArgs()


    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(args.playlist)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbar.title = getString(R.string.edit_playlist)
        binding.btnCreate.text = getString(R.string.save)
    }
}