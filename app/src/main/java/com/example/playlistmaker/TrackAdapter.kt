package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val trackName: TextView = itemView.findViewById(R.id.track_name)
        private val artistName: TextView = itemView.findViewById(R.id.artist_name)
        private val trackTime: TextView = itemView.findViewById(R.id.track_time)
        private val artwork: ImageView = itemView.findViewById(R.id.track_artwork)

        fun bind(track: Track) {
            trackName.text = track.trackName
            artistName.text = track.artistName
            trackTime.text = track.getFormattedTime()

            // Загрузка обложки с Glide
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.placeholder) // Заглушка
                .error(R.drawable.placeholder) // Заглушка при ошибке
                .into(artwork)

            itemView.setOnClickListener {
                onTrackClick(track)
            }
        }
    }
}