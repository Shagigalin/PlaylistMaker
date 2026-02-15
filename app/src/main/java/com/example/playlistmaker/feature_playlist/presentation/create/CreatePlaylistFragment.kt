package com.example.playlistmaker.feature_playlist.presentation.create

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.feature_playlist.presentation.model.CreatePlaylistState
import com.example.playlistmaker.utils.CustomToast
import com.example.playlistmaker.utils.ImageUtils
import com.example.playlistmaker.utils.StoragePermissionHelper
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private lateinit var permissionHelper: StoragePermissionHelper

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                handleImageSelected(uri)
            }
        }
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

        permissionHelper = StoragePermissionHelper(this)

        setupViews()
        setupObservers()
        handleBackPress()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }

        binding.coverContainer.setOnClickListener {
            permissionHelper.checkStoragePermission {
                openImagePicker()
            }
        }

        binding.etName.doAfterTextChanged { text ->
            viewModel.updateName(text.toString())
        }

        binding.etDescription.doAfterTextChanged { text ->
            viewModel.updateDescription(text.toString())
        }

        binding.btnCreate.setOnClickListener {
            viewModel.createPlaylist()
        }
    }

    private fun setupObservers() {
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

                CustomToast.show(requireContext(), getString(R.string.playlist_created, it))
                viewModel.onSuccessMessageShown()
            }
        }

        viewModel.showExitDialog.observe(viewLifecycleOwner) { show ->
            if (show) {
                showExitConfirmationDialog()
                viewModel.onExitDialogDismissed()
            }
        }
    }

    private fun updateUI(state: CreatePlaylistState) {
        binding.btnCreate.isEnabled = state.isNameValid && !state.isSaving

        if (state.isSaving) {
            binding.btnCreate.text = "Сохранение..."
        } else {
            binding.btnCreate.text = getString(R.string.create)
        }

        if (state.coverUri != null) {
            showSelectedImage(state.coverUri)
        }

        state.error?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun handleImageSelected(uri: Uri) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.coverContainer.isEnabled = false
            try {
                when (val result = ImageUtils.copyImageToPrivateStorage(requireContext(), uri)) {
                    is ImageUtils.ImageCopyResult.Success -> {
                        viewModel.updateCover(uri, result.path)
                    }
                    is ImageUtils.ImageCopyResult.Error -> {
                        Toast.makeText(
                            requireContext(),
                            result.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.coverContainer.isEnabled = true
            }
        }
    }

    private fun showSelectedImage(uri: Uri) {
        binding.coverPlaceholder.visibility = View.GONE
        binding.ivCover.visibility = View.VISIBLE

        Glide.with(this)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.ivCover)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.exit_without_saving_title)
            .setMessage(R.string.exit_without_saving_message)
            .setPositiveButton(R.string.finish) { _, _ ->
                viewModel.onExitConfirmed()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}