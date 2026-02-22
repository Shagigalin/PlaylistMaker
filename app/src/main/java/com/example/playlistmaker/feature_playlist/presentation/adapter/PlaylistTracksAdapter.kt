package com.example.playlistmaker.feature_playlist.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ItemTrackBinding
import com.example.playlistmaker.feature_search.domain.model.Track

class PlaylistTracksAdapter(
    private val onItemClick: (Track) -> Unit,
    private val onItemLongClick: (Track) -> Unit
) : ListAdapter<Track, PlaylistTracksAdapter.TrackViewHolder>(TrackDiffCallback()) {

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
        private val onItemClick: (Track) -> Unit,
        private val onItemLongClick: (Track) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.apply {
                trackName.text = track.trackName
                artistName.text = track.artistName
                trackTime.text = track.getFormattedTime()


                val artworkUrl = track.artworkUrl100.replace("100x100", "45x45")
                Glide.with(itemView.context)
                    .load(artworkUrl)
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

    class TrackDiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.trackId == newItem.trackId
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
}