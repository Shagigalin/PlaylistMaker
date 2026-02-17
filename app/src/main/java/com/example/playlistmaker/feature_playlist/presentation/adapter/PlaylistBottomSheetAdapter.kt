package com.example.playlistmaker.feature_playlist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.example.playlistmaker.feature_playlist.domain.model.Playlist
import java.io.File

class PlaylistBottomSheetAdapter(
    private val onItemClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistBottomSheetAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBottomSheetBinding.inflate(
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
        private val binding: ItemPlaylistBottomSheetBinding,
        private val onItemClick: (Playlist) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            binding.apply {
                tvPlaylistName.text = playlist.name
                tvTrackCount.text = getTrackCountText(playlist.trackCount)

                if (playlist.coverPath != null) {
                    val file = File(playlist.coverPath)
                    if (file.exists()) {
                        Glide.with(itemView.context)
                            .load(file)
                            .centerCrop()
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .into(ivPlaylistCover)
                    } else {
                        ivPlaylistCover.setImageResource(R.drawable.placeholder)
                    }
                } else {
                    ivPlaylistCover.setImageResource(R.drawable.placeholder)
                }

                root.setOnClickListener {
                    onItemClick(playlist)
                }
            }
        }

        private fun getTrackCountText(count: Int): String {

            return itemView.context.resources.getQuantityString(
                R.plurals.tracks_count,
                count,
                count
            )
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