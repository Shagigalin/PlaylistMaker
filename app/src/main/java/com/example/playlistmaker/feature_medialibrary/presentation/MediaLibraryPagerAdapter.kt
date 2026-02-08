package com.example.playlistmaker.feature_medialibrary.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.feature_medialibrary.presentation.favorites.FavoritesFragment
import com.example.playlistmaker.feature_medialibrary.presentation.playlist.PlaylistFragment

class MediaLibraryPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment()
            1 -> PlaylistFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}