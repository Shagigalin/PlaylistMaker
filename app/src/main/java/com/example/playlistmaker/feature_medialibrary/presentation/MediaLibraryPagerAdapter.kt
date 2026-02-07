package com.example.playlistmaker.feature_medialibrary.presentation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.feature_medialibrary.presentation.favorites.FavoritesFragment
import com.example.playlistmaker.feature_medialibrary.presentation.playlist.PlaylistFragment

class MediaLibraryPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment()
            1 -> PlaylistFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}