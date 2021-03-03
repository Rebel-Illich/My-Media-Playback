package com.hunter.mymediaplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SongsFragment extends Fragment implements  SongsAdapter.OnShareClickedListener, MyCallback {

    private ArrayList<Track> songsArrayList;
    private RecyclerView recyclerView;
    private SongsAdapter songsAdapter;

    private IPlayerController playerController;


    public SongsFragment(IPlayerController iPlayerController ) {
        playerController = iPlayerController;
        playerController.setSongsFragments(this);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            songsArrayList.remove(viewHolder.getAdapterPosition());
            songsAdapter.notifyDataSetChanged();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_songs, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);
        generateData();
        setData();
    }

    private void init(View view) {
        songsAdapter = new SongsAdapter(this);
        songsArrayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(songsAdapter);
        playerController.ListOfSongs(songsArrayList);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songsArrayList.removeAll(songsArrayList);
                songsArrayList.add(new Track("Track 1", "Bang", R.drawable.t1, R.raw.song5));
                songsArrayList.add(new Track("Track 2", "Ich will", R.drawable.t2, R.raw.song2));
                songsArrayList.add(new Track("Track 3", "Du hast", R.drawable.t3, R.raw.song3));
                songsArrayList.add(new Track("Track 4", "Green star liner", R.drawable.t4, R.raw.song));
                songsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setData() {
        songsAdapter.setMovieArrayList(songsArrayList);
        songsAdapter.notifyDataSetChanged();
    }

    private void generateData() {
        songsArrayList.add(new Track("Track 1", "Bang", R.drawable.t1, R.raw.song5));
        songsArrayList.add(new Track("Track 2", "Ich will", R.drawable.t2, R.raw.song2));
        songsArrayList.add(new Track("Track 3", "Du hast", R.drawable.t3, R.raw.song3));
        songsArrayList.add(new Track("Track 4", "Green star liner", R.drawable.t4, R.raw.song));
    }

    @Override
    public void itemClicked(int position) {
        playerController.play(position);
    }

    public void notifyListChanged() {
        songsAdapter.notifyDataSetChanged();
    }
}
