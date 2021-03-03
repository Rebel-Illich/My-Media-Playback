package com.hunter.mymediaplayer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import static com.hunter.mymediaplayer.CreateNotification.ACTION_NEXT;
import static com.hunter.mymediaplayer.CreateNotification.ACTION_PLAY;
import static com.hunter.mymediaplayer.CreateNotification.ACTION_PREVIUOS;
import static com.hunter.mymediaplayer.PlayerFragment.SEEK_POSITION;
import static com.hunter.mymediaplayer.PlayerFragment.SET_TRACK_ACTION;
import static com.hunter.mymediaplayer.PlayerFragment.TRACK;


public class MusicPlayerService extends Service {

    private static final String TAG = "MusicPlayerService";
    MediaPlayer mediaPlayer;
    boolean isMediaPlayer = false;
    boolean isPlaying = false;
    NotificationManager notificationManager;

    Thread updateSeekBar;
    private Track currentTrack;
    int currentIndex = 0;


    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter(ACTION_PLAY));
            registerReceiver(broadcastReceiver, new IntentFilter(ACTION_PREVIUOS));
            registerReceiver(broadcastReceiver, new IntentFilter(ACTION_NEXT));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive() called with: context = [" + context + "], intent = [" + intent + "]");
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case ACTION_PREVIUOS:
                    onTrackPrevious();
                    break;
                case ACTION_PLAY:
                    if (isPlaying){
                        onTrackPause();
                    } else {
                        onTrackPlay();
                    }
                    break;
                case ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };

    private void createChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                        "KOD Dev", NotificationManager.IMPORTANCE_LOW);

                notificationManager = getSystemService(NotificationManager.class);
                if (notificationManager != null){
                    notificationManager.createNotificationChannel(channel);
                }
            }
    }


    private void reInitPlayer() {
        setPreparedListener();

        setCompletedListener();
    }

    private void setCompletedListener() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("music_completed", "music_completed");
                // music is completed and you can go the next music
                onCompleteTrack();
            }
        });
    }

    private void onCompleteTrack() {
        Intent intentCompleteTrack = new Intent("TRACK_FINISHED");
        Bundle extras = new Bundle();
        extras.putParcelable(TRACK, currentTrack);
        intentCompleteTrack.putExtras(extras);
        sendBroadcast(intentCompleteTrack);
    }

    private void setPreparedListener() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                isMediaPlayer = true;
                sendDurationMessage(mp.getDuration());
                startProgressReciver();
                Log.d(TAG, "onPrepared: media is ready");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() called with: intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        switch (intent.getAction()) {
            case SET_TRACK_ACTION:
                this.currentTrack = intent.getParcelableExtra(TRACK);
                restartPlayer();
                break;
            case "START":
            isPlaying = true;
            startProgressReciver();
            mediaPlayer.start();
                onTrackPlay();
            break;
            case "STOP":
                mediaPlayer.stop();
                mediaPlayer.release();
                updateSeekBar = null;
                isPlaying = false;
                break;
            case "PAUSE":
                mediaPlayer.pause();
                onTrackPause();
                updateSeekBar = null;
                isPlaying = false;
                break;
            case "NEXT":
                onTrackNext();
                break;
            case "PREV":
                onTrackPrevious();
                break;
            case "SEEK":
                int position = intent.getIntExtra(SEEK_POSITION, 0);
                mediaPlayer.seekTo(position * 1000);
                break;
            case ACTION_PREVIUOS:
                onTrackPrevious();
                break;
            case ACTION_PLAY:
                if (isPlaying) {
                    mediaPlayer.pause();
                    onTrackPause();
                    updateSeekBar = null;
                    isPlaying = false;
                } else {
                    isPlaying = true;
                    startProgressReciver();
                    mediaPlayer.start();
                    updateSeekBar.start();
                    onTrackPlay();
                }
                break;
            case ACTION_NEXT:
                requestNextTrack();
                onTrackNext();
                break;
            default:
                break;

        }
        return START_NOT_STICKY;
    }

    private void requestNextTrack() {
        Intent intent = new Intent("REQUEST_NEXT");
        sendBroadcast(intent);
    }

    private void onTrackNext() {
        CreateNotification.createNotification(MusicPlayerService.this, currentTrack,
                R.drawable.ic_baseline_pause_circle_filled_24, currentIndex, 1);
    }

    private void onTrackPrevious() {
        CreateNotification.createNotification(MusicPlayerService.this, currentTrack,
                R.drawable.ic_baseline_pause_circle_filled_24, currentIndex, 1);
    }

    private void onTrackPause() {
        CreateNotification.createNotification(MusicPlayerService.this, currentTrack,
                R.drawable.ic_play_arrow_black_24dp, currentIndex, 1);
        isPlaying = false;
    }

    private void onTrackPlay() {
        CreateNotification.createNotification(MusicPlayerService.this, currentTrack,
                R.drawable.ic_baseline_pause_circle_filled_24, currentIndex, 1);
      // title.setText(tracks.get(currentIndex).getTitle());
        isPlaying = true;
    }

    private void startProgressReciver() {
        updateSeekBar = new Thread() {
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (isPlaying && currentPosition < totalDuration) {
                    try {
                        Log.d(TAG, "run() called");
                        sleep(500);
                        if (mediaPlayer != null) {
                            currentPosition = mediaPlayer.getCurrentPosition();
                            sendPositionMessage(currentPosition);
                        }
                    } catch (InterruptedException e) {

                    }
                }
            }
        };
    }

    void sendPositionMessage(int time) {
        Log.d(TAG, "sendPositionMessage() called with: time = [" + time + "]");
        Intent intent = new Intent(PlayerFragment.SEEKER_POSITION_ACTION);
        intent.putExtra(PlayerFragment.PLAYER_TIME, time / 1000);
        sendBroadcast(intent);
    }

    void sendDurationMessage(int time) {
        Log.d(TAG, "sendDurationMessage() called with: time = [" + time + "]");
        Intent intent = new Intent(PlayerFragment.SEEKBAR_DURATION_ACTION);
        intent.putExtra(PlayerFragment.PLAYER_DURATION, time / 1000);
        sendBroadcast(intent);
    }

    private void restartPlayer() {
        Log.d(TAG, "restartPlayer() called");
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), currentTrack.getAudio());
        reInitPlayer();
        mediaPlayer.start();
        restartSeekBarThread();
        onTrackPlay();
    }

    private void restartSeekBarThread() {
        Log.d(TAG, "restartSeekBarThread() called");
        if (updateSeekBar != null) {
            updateSeekBar.interrupt();
        }
        updateSeekBar = null;
        startProgressReciver();
        updateSeekBar.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearMediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
    }

    private void clearMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

