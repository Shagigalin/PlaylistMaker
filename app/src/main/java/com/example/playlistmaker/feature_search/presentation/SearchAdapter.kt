package com.example.playlistmaker.feature_search.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.feature_search.domain.model.Track

class SearchAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<SearchAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    class TrackViewHolder(
        itemView: View,
        private val onTrackClick: (Track) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.artist_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)
        private val artwork: ImageView = itemView.findViewById(R.id.track_artwork)

        private var currentTrack: Track? = null

        init {
            itemView.setOnClickListener {
                currentTrack?.let { track ->
                    onTrackClick(track)
                }
            }
        }

        fun bind(track: Track) {
            currentTrack = track
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.getFormattedTime()

            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(artwork)
        }
    }
}