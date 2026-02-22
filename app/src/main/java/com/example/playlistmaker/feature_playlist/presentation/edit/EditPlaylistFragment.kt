package com.example.playlistmaker.feature_playlist.presentation.edit

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import com.example.playlistmaker.feature_playlist.presentation.create.CreatePlaylistFragment
import com.example.playlistmaker.feature_playlist.presentation.model.CreatePlaylistState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

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


        fillPlaylistData(args.playlist)
    }

    private fun fillPlaylistData(playlist: Playlist) {

        binding.etName.setText(playlist.name)


        if (!playlist.description.isNullOrBlank()) {
            binding.etDescription.setText(playlist.description)
        }


        if (!playlist.coverPath.isNullOrBlank()) {
            val file = File(playlist.coverPath)
            if (file.exists()) {
                val uri = Uri.fromFile(file)
                showCoverImage(uri)

                viewModel.updateCover(uri, playlist.coverPath)
            } else {
                showPlaceholderImage()
            }
        } else {
            showPlaceholderImage()
        }
    }

    private fun showCoverImage(uri: Uri) {
        binding.coverPlaceholder.visibility = View.GONE
        binding.ivCover.visibility = View.VISIBLE

        Glide.with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.ivCover)
    }

    private fun showPlaceholderImage() {
        binding.coverPlaceholder.visibility = View.VISIBLE
        binding.ivCover.visibility = View.GONE
    }

    override fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigateUp()
                viewModel.onNavigateBackHandled()
            }
        }

        viewModel.showSuccessMessage.observe(viewLifecycleOwner) { playlistName ->
            playlistName?.let {

                viewModel.onSuccessMessageShown()
            }
        }
    }

    override fun updateUI(state: CreatePlaylistState) {
        binding.btnCreate.isEnabled = state.isNameValid && !state.isSaving

        if (state.isSaving) {
            binding.btnCreate.text = getString(R.string.saving)
        } else {
            binding.btnCreate.text = getString(R.string.save)
        }


        state.error?.let {

        }
    }
}