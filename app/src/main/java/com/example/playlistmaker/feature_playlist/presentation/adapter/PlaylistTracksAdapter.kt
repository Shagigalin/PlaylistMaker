package com.example.playlistmaker.feature_playlist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.feature_playlist.presentation.model.TrackDisplay

class PlaylistTracksAdapter(
    private val onItemClick: (TrackDisplay) -> Unit,
    private val onItemLongClick: (TrackDisplay) -> Unit
) : ListAdapter<TrackDisplay, PlaylistTracksAdapter.TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackViewHolder(binding, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrackViewHolder(
        private val binding: ItemTrackBinding,
        private val onItemClick: (TrackDisplay) -> Unit,
        private val onItemLongClick: (TrackDisplay) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackDisplay) {
            binding.apply {
                trackName.text = track.trackName
                artistName.text = track.artistName
                trackTime.text = track.trackTime


                Glide.with(itemView.context)
                    .load(track.artworkUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerCrop()
                    .into(trackArtwork)

                root.setOnClickListener {
                    onItemClick(track)
                }

                root.setOnLongClickListener {
                    onItemLongClick(track)
                    true
                }
            }
        }
    }

    class TrackDiffCallback : DiffUtil.ItemCallback<TrackDisplay>() {
        override fun areItemsTheSame(oldItem: TrackDisplay, newItem: TrackDisplay): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(oldItem: TrackDisplay, newItem: TrackDisplay): Boolean {
            return oldItem == newItem
        }
    }
}