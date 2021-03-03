package com.hunter.mymediaplayer;

import java.util.ArrayList;

public interface IPlayerController {
    void stop();

    void play(int position);

    void pause();

    void ListOfSongs(ArrayList<Track> songsArrayList);

    void nextSong();

    void prevSong();

    void setPlayerFragment(PlayerFragment playerFragment);

    void setSongsFragments(SongsFragment songsFragment);

    void onTrackFinished();

    void onTrackListFinished();

    void start();

    void seek(int progress);
}
