package com.hunter.mymediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hunter.mymediaplayer.PlayerFragment.PLAYER_DURATION;
import static com.hunter.mymediaplayer.PlayerFragment.PLAYER_TIME;
import static com.hunter.mymediaplayer.PlayerFragment.SEEKBAR_DURATION_ACTION;
import static com.hunter.mymediaplayer.PlayerFragment.SEEKER_POSITION_ACTION;
import static com.hunter.mymediaplayer.PlayerFragment.SEEK_POSITION;
import static com.hunter.mymediaplayer.PlayerFragment.SET_TRACK_ACTION;
import static com.hunter.mymediaplayer.PlayerFragment.TRACK;

public class PlayerController implements IPlayerController {

    private static final String TAG = "BroadCast";
    private PlayerFragment playerFragment;
    private ArrayList<Track> songsArrayList;
    private MainActivity mainActivity;
    private int currentPosition = -1;
    private BroadcastReceiver br;
    private SongsFragment songsFragment;

    public PlayerController(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
                String action = intent.getAction();
                if (action != null) {
                    switch (action) {
                        case SEEKER_POSITION_ACTION: {
                            int playerTime = intent.getExtras().getInt(PLAYER_TIME);
                            Log.d(TAG, "onReceive: SEEKER_POSITION_ACTION " + playerTime);
                            playerFragment.setProgress(playerTime);
                            break;
                        }
                        case SEEKBAR_DURATION_ACTION: {
                            int playerDuration = intent.getExtras().getInt(PLAYER_DURATION);
                            Log.d(TAG, "onReceive: SEEKBAR_DURATION_ACTION " + playerDuration);
                            playerFragment.setMax(playerDuration);
                            break;
                        }
                        case "REQUEST_NEXT" : {
                            nextSong();
                            break;
                        }
                        case "TRACK_FINISHED":
                            onTrackFinished();
                            break;
                    }
                }
            }
        };
        mainActivity.handleBroadcastReceiver(br);
    }

    @Override
    public void stop() {
        Intent intent = new Intent( mainActivity, MusicPlayerService.class);
        intent.setAction("STOP");
        mainActivity.stopService(intent);
    }

    @Override
    public void play(int position) {
        this.currentPosition = position;
        songsFragment.notifyListChanged();
        Intent playIntent = new Intent(mainActivity, MusicPlayerService.class);
        playIntent.setAction(SET_TRACK_ACTION);
        Bundle bundle = new Bundle();
        bundle.putParcelable(TRACK, songsArrayList.get(position));
        playIntent.putExtras(bundle);
        mainActivity.startService(playIntent);
        playerFragment.setPlayingTrack(songsArrayList.get(position));
        Collections.swap(songsArrayList, currentPosition, 0);
    }

    @Override
    public void pause() {
        sendServiceCommand("PAUSE", null);
    }

    @Override
    public void ListOfSongs(ArrayList<Track> songsArrayList) {
        this.songsArrayList = songsArrayList;
    }

    @Override
    public void nextSong() {
        if (currentPosition < songsArrayList.size() - 1) {
            play(++currentPosition);
        }
    }

    @Override
    public void prevSong() {
        if (currentPosition > 0) {
            play(--currentPosition);
        }
    }

    @Override
    public void setPlayerFragment(PlayerFragment playerFragment) {
        this.playerFragment = playerFragment;
    }

    @Override
    public void setSongsFragments(SongsFragment songsFragment) {
        this.songsFragment = songsFragment;
    }

    @Override
    public void onTrackFinished() {
        List<Track> sublist = new ArrayList<>(songsArrayList.subList(0, currentPosition));
        songsArrayList.removeAll(sublist);
        songsArrayList.addAll(sublist);
        songsFragment.notifyListChanged();
        play(currentPosition);
    }

    @Override
    public void onTrackListFinished() {
        if(currentPosition == songsArrayList.size() - 1){
            currentPosition = 0;
            play(currentPosition);
        }
    }

    @Override
    public void start() {
        if (currentPosition != -1) {
            sendServiceCommand("START", null);
        } else {
            currentPosition = 0;
            play(currentPosition);
        }
    }

    @Override
    public void seek(int progress) {
        Bundle extras = new Bundle();
        extras.putInt(SEEK_POSITION, progress);
        sendServiceCommand("SEEK", extras);
    }

    private void sendServiceCommand(String action, Bundle params){
        Intent intent = new Intent( mainActivity, MusicPlayerService.class);
        intent.setAction(action);
        if (params != null) {
            intent.putExtras(params);
        }
        mainActivity.startService(intent);
    }
}

