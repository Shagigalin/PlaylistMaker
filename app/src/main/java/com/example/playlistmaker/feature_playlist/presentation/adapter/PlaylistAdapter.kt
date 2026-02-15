package com.example.playlistmaker.feature_playlist.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBinding
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import java.io.File

class PlaylistAdapter(
    private val onItemClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding,
        private val onItemClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.apply {
                tvPlaylistName.text = playlist.name
                tvTrackCount.text = getTrackCountText(playlist.trackCount)

                if (!playlist.coverPath.isNullOrEmpty()) {
                    val file = File(playlist.coverPath)
                    if (file.exists()) {
                        Glide.with(itemView.context)
                            .load(file)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(ivPlaylistCover)

                        ivPlaylistCover.visibility = View.VISIBLE
                        ivPlaylistPlaceholder.visibility = View.GONE
                    } else {
                        showPlaceholder()
                    }
                } else {
                    showPlaceholder()
                }

                root.setOnClickListener {
                    onItemClick(playlist)
                }
            }
        }

        private fun showPlaceholder() {
            binding.ivPlaylistCover.visibility = View.GONE
            binding.ivPlaylistPlaceholder.visibility = View.VISIBLE
        }

        private fun getTrackCountText(count: Int): String {
            return when {
                count % 100 in 11..19 -> "$count треков"
                count % 10 == 1 -> "$count трек"
                count % 10 in 2..4 -> "$count трека"
                else -> "$count треков"
            }
        }
    }

    class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem == newItem
        }
    }
}