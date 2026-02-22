package com.example.playlistmaker.feature_playlist.presentation.details

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.core.NavigationController
import com.example.playlistmaker.databinding.FragmentPlaylistDetailsBinding
import com.example.playlistmaker.feature_playlist.domain.model.PlaylistDetails
import com.example.playlistmaker.feature_playlist.presentation.adapter.PlaylistTracksAdapter
import com.example.playlistmaker.feature_playlist.presentation.model.TrackDisplay
import com.example.playlistmaker.feature_search.domain.model.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistDetailsFragment : Fragment() {

    private var _binding: FragmentPlaylistDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistDetailsFragmentArgs by navArgs()
    private val viewModel: PlaylistDetailsViewModel by viewModel {
        parametersOf(args.playlistId)
    }

    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var tracksAdapter: PlaylistTracksAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (requireActivity() as? NavigationController)?.showBottomNavigation(false)

        setupBottomSheets()
        setupViews()
        observeViewModel()

        viewModel.loadPlaylistDetails(args.playlistId)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        (requireActivity() as? NavigationController)?.showBottomNavigation(true)
        _binding = null
    }

    private fun setupBottomSheets() {

        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false



        }

        tracksBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Ничего не делаем
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding != null) {
                    binding.overlay.alpha = slideOffset * 0.6f
                    binding.overlay.visibility = View.VISIBLE
                }
            }
        })

        // Bottom Sheet для меню
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (_binding != null) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                            binding.overlay.visibility = View.GONE
                            viewModel.hideMenu()
                        }
                        else -> {
                            binding.overlay.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (_binding != null) {
                    binding.overlay.alpha = slideOffset * 0.6f
                }
            }
        })


        tracksAdapter = PlaylistTracksAdapter(
            onItemClick = { trackDisplay ->

                val details = (viewModel.state.value as? PlaylistDetailsState.Content)?.details
                val originalTrack = details?.tracks?.find { it.trackId == trackDisplay.trackId }
                originalTrack?.let { viewModel.onTrackClick(it) }
            },
            onItemLongClick = { trackDisplay ->
                val details = (viewModel.state.value as? PlaylistDetailsState.Content)?.details
                val originalTrack = details?.tracks?.find { it.trackId == trackDisplay.trackId }
                originalTrack?.let { viewModel.onTrackLongClick(it) }
            }
        )

        binding.rvTracks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tracksAdapter
        }


        binding.menuSheet.menuShare.setOnClickListener {
            viewModel.onMenuShareClick()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.menuSheet.menuEdit.setOnClickListener {
            viewModel.onMenuEditClick()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.menuSheet.menuDelete.setOnClickListener {
            viewModel.onMenuDeleteClick()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener {
            viewModel.onBackPressed()
        }

        binding.btnShare.setOnClickListener {
            viewModel.onMenuShareClick()
        }

        binding.btnMenu.setOnClickListener {
            viewModel.showMenu()
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.overlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun observeViewModel() {
        viewModel.navigateToEdit.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                val details = (viewModel.state.value as? PlaylistDetailsState.Content)?.details
                details?.let {
                    try {
                        val action = PlaylistDetailsFragmentDirections
                            .actionPlaylistDetailsFragmentToEditPlaylistFragment(it.playlist)
                        findNavController().navigate(action)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Ошибка навигации: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistDetailsState.Loading -> showLoading()
                is PlaylistDetailsState.Content -> showContent(state.details)
                is PlaylistDetailsState.Error -> showError(state.message)
            }
        }

        viewModel.tracksForDisplay.observe(viewLifecycleOwner) { tracks ->
            tracksAdapter.submitList(tracks)
        }

        viewModel.navigateBack.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigateUp()
            }
        }

        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            track?.let {
                val action = PlaylistDetailsFragmentDirections
                    .actionPlaylistDetailsFragmentToPlayerFragment(it)
                findNavController().navigate(action)
            }
        }

        viewModel.showDeleteTrackDialog.observe(viewLifecycleOwner) { track ->
            track?.let {
                showDeleteTrackDialog(it)
            }
        }

        viewModel.showDeletePlaylistDialog.observe(viewLifecycleOwner) { show ->
            if (show) {
                showDeletePlaylistDialog()
            }
        }

        viewModel.shareText.observe(viewLifecycleOwner) { text ->
            text?.let {
                sharePlaylist(it)
            }
        }

        viewModel.showEmptyPlaylistToast.observe(viewLifecycleOwner) { show ->
            if (show) {
                Toast.makeText(
                    requireContext(),
                    "В этом плейлисте нет списка треков, которым можно поделиться",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        viewModel.isMenuVisible.observe(viewLifecycleOwner) { isVisible ->
            if (isVisible) {
                updateMenuContent()
            }
        }
    }

    private fun updateMenuContent() {
        val details = (viewModel.state.value as? PlaylistDetailsState.Content)?.details ?: return

        binding.menuSheet.tvMenuPlaylistName.text = details.playlist.name
        binding.menuSheet.tvMenuTrackCount.text = resources.getQuantityString(
            R.plurals.tracks_count,
            details.trackCount,
            details.trackCount
        )

        if (!details.playlist.coverPath.isNullOrBlank()) {
            val file = File(details.playlist.coverPath)
            if (file.exists()) {
                Glide.with(this)
                    .load(file)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.menuSheet.ivMenuCover)
            } else {
                binding.menuSheet.ivMenuCover.setImageResource(R.drawable.placeholder)
            }
        } else {
            binding.menuSheet.ivMenuCover.setImageResource(R.drawable.placeholder)
        }
    }

    private fun sharePlaylist(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться плейлистом"))
    }

    private fun showDeleteTrackDialog(track: Track) {
        val theme = if (isDarkTheme()) R.style.AlertDialogTheme_Dark else R.style.AlertDialogTheme_Light

        AlertDialog.Builder(requireContext(), theme)
            .setTitle("Удалить трек")
            .setMessage("Хотите удалить трек \"${track.trackName}\"?")
            .setPositiveButton("ДА") { _, _ ->
                viewModel.deleteTrackFromPlaylist(track)
            }
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        val theme = if (isDarkTheme()) R.style.AlertDialogTheme_Dark else R.style.AlertDialogTheme_Light

        AlertDialog.Builder(requireContext(), theme)
            .setTitle("Удалить плейлист")
            .setMessage("Вы уверены, что хотите удалить этот плейлист?")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deletePlaylist()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun isDarkTheme(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    private fun showLoading() {
        // TODO: добавить ProgressBar
    }

    private fun showContent(details: PlaylistDetails) {
        val playlist = details.playlist

        binding.tvPlaylistName.text = playlist.name

        if (!playlist.description.isNullOrBlank()) {
            binding.tvPlaylistDescription.text = playlist.description
            binding.tvPlaylistDescription.isVisible = true
        } else {
            binding.tvPlaylistDescription.isVisible = false
        }

        binding.tvTotalDuration.text = viewModel.formatDuration(details.totalDuration)
        binding.tvTrackCount.text = resources.getQuantityString(
            R.plurals.tracks_count,
            details.trackCount,
            details.trackCount
        )

        if (!playlist.coverPath.isNullOrBlank()) {
            val file = File(playlist.coverPath)
            if (file.exists()) {
                Glide.with(this)
                    .load(file)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(binding.ivPlaylistCover)
            } else {
                binding.ivPlaylistCover.setImageResource(R.drawable.placeholder)
            }
        } else {
            binding.ivPlaylistCover.setImageResource(R.drawable.placeholder)
        }

        if (details.tracks.isEmpty()) {
            binding.tvEmptyTracks.isVisible = true
            binding.rvTracks.isVisible = false
        } else {
            binding.tvEmptyTracks.isVisible = false
            binding.rvTracks.isVisible = true

        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}