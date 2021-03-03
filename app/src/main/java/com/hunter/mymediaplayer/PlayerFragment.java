package com.hunter.mymediaplayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlayerFragment extends Fragment  implements View.OnClickListener{
    private static final String TAG = "PlayerFragment";
    public static final String SEEKER_POSITION_ACTION = "SEEKBAR_POSITION_ACTION";
    public static final String SEEKBAR_DURATION_ACTION = "SEEKBAR_DURATION_ACTION";
    public static final String SEEK_POSITION = "SEEK_POSITION";
    public static final String PLAYER_TIME = "PLAYER_TIME";
    public static final String PLAYER_DURATION = "PLAYER_DURATION";
    public static final String SET_TRACK_ACTION = "SET_TRACK_ACTION";
    public static final String TRACK = "TRACK";

    SeekBar sb;
    private Button start;
    private Button stop;
    private Button pause;
    Button previous, next;
    TextView songNameText;
    TextView playerDuration, playerTotal;
    Boolean buttonFlag = true;
    MediaPlayer mediaPlayer;
    boolean wasPlaying = false;
    BroadcastReceiver br;
    private String LOG_TAG = "My Media";
    private IPlayerController playerController;


    public PlayerFragment(IPlayerController iPlayerController ) {
        playerController = iPlayerController;
        playerController.setPlayerFragment(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        songNameText = (TextView) view.findViewById(R.id.txtSongLabel);

        songNameText = (TextView) view.findViewById(R.id.txtSongLabel);

        start = (Button) view.findViewById(R.id.play);
        pause = (Button) view.findViewById(R.id.pause);
        stop = (Button) view.findViewById(R.id.stop);
        next = (Button) view.findViewById(R.id.next);
        previous = view.findViewById(R.id.previous);
        playerDuration = (TextView)  view.findViewById(R.id.playerCurrentTimeText);
        playerTotal = (TextView)  view.findViewById(R.id.playerTotalTimeText);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        sb = view.findViewById(R.id.seekBar);

        initilizeViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().registerReceiver(br, new IntentFilter(SEEKBAR_DURATION_ACTION));
        requireActivity().registerReceiver(br, new IntentFilter(SEEKER_POSITION_ACTION));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    private void unregisterReceiver(BroadcastReceiver br) {
       requireActivity().unregisterReceiver(br);
    }

    private void initilizeViews() {

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    playerController.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

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
                            sb.setProgress(playerTime);
                            break;
                        }
                        case SEEKBAR_DURATION_ACTION: {
                            int playerDuration = intent.getExtras().getInt(PLAYER_DURATION);
                            Log.d(TAG, "onReceive: SEEKBAR_DURATION_ACTION " + playerDuration);
                            sb.setMax(playerDuration);
                            break;
                        }
                    }
                }

                Log.d(LOG_TAG, "onReceive: task = ");
            }
        };
    }

    public void setPlayingTrack(Track track) {
        this.songNameText.setText(track.getTitle());
        start.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                start.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
                playerController.start();
                break;
            case R.id.pause:
                pause.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);
                playerController.pause();
                break;
            case R.id.stop:
               playerController.stop();
                break;
            case R.id.next:
                playerController.onTrackListFinished();
                playerController.nextSong();
                break;
            case R.id.previous:
                playerController.prevSong();
                break;
        }
    }

    public void setMax(int playerDuration) {
        playerTotal.setText(getTime(playerDuration));
        sb.setMax(playerDuration);
    }

    public void setProgress(int progress) {
        sb.setProgress(progress);
        playerDuration.setText(getTime(progress));
    }

    private String getTime(int totalSecs) {
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        String timeString = String.format("%02d:%02d", minutes, seconds);
        return timeString;
    }
}