package com.hunter.mymediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.MovieViewHolder> implements View.OnClickListener {
    private ArrayList<Track> songsList = new ArrayList<>();
    private MyCallback mCallback;
    private ArrayList<Track> songsArrayList = new ArrayList<>();

    public SongsAdapter(MyCallback mCallback) {
        this.mCallback = mCallback;
    }

    public void setMovieList(ArrayList<Track> list) {
        this.songsList = list;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.songs_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Track track = songsArrayList.get(position);
        holder.txtTitle.setText(track.getTitle());
        holder.txtArtist.setText(track.getArtist());
        holder.itemView.setOnClickListener(v -> {
            mCallback.itemClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return songsArrayList.size();
    }

    @Override
    public void onClick(View v) {

    }

    public void setMovieArrayList(ArrayList<Track> movieArrayList) {
        this.songsArrayList = movieArrayList;
    }

    public ArrayList<Track> getMovieArrayList() {
        return songsArrayList;
    }

    public interface OnShareClickedListener {

    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtArtist;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtArtist = itemView.findViewById(R.id.txt_artist);
        }
    }
}