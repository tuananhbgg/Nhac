package com.example.nghenhac;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.nghenhac.model.SongModel;
import com.example.nghenhac.view.MainActivity;
import com.example.nghenhac.view.PlayAudioActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static com.example.nghenhac.view.MainActivity.models;
import static com.example.nghenhac.view.MainActivity.positionSong;
import static com.example.nghenhac.view.MainActivity.tvSN;
import static com.example.nghenhac.view.PlayAudioActivity.imgPlay;
import static com.example.nghenhac.view.PlayAudioActivity.imgPlayType;
import static com.example.nghenhac.view.PlayAudioActivity.playType;

public class PlayMusicService extends Service {
    private PendingIntent pendingIntent;
    public MusicBroadcast musicBroadcast;
    private String AUDIO_ACTION = "Audio_Action";
    private String BUTTON_CLICK = "buttonClick";
    private Intent testIntent;
    private PlayAudioActivity audioActivity;
    public static ArrayList<SongModel> listSongModels;
    public static MediaPlayer player;
    public static String CHANNEL_ID = "NgheNhacDemo";
    public static NotificationCompat.Builder notification;
    public static NotificationManager notificationManager;
    public static Intent notificationIntent;
    public static RemoteViews remoteViews;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        musicBroadcast = new MusicBroadcast();
        IntentFilter intentFilter = new IntentFilter(AUDIO_ACTION);
        registerReceiver(musicBroadcast, intentFilter);
        testIntent = new Intent();
        if (player != null) {
            player.stop();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        audioActivity = new PlayAudioActivity();
        listSongModels = MainActivity.models;
        playAudio(positionSong);
        createNotification();
        return START_NOT_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotification() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.small_notification);
        notificationIntent = new Intent(this, PlayAudioActivity.class);
        notificationIntent.putExtra("position", positionSong);
        notificationIntent.putExtra("duration", player.getDuration());
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews = new RemoteViews(getPackageName(), R.layout.small_notification);
        remoteViews.setTextViewText(R.id.songName, listSongModels.get(positionSong).getSongName());
        remoteViews.setImageViewResource(R.id.play, R.drawable.ic_pause_black);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel");
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setCustomContentView(remoteViews)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music);
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            remoteViews.setOnClickPendingIntent(R.id.rlAudioPlaying, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.pre, onButtonPreNotificationClick());
            remoteViews.setOnClickPendingIntent(R.id.play, onButtonPlayNotificationClick());
            remoteViews.setOnClickPendingIntent(R.id.next, onButtonNextNotificationClick());
            remoteViews.setOnClickPendingIntent(R.id.close, onButtonCloseNotificationClick());
            startForeground(1, notification.build());

        } else {
            notification = new NotificationCompat.Builder(getApplicationContext())
                    .setCustomContentView(remoteViews)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_music);
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            remoteViews.setOnClickPendingIntent(R.id.rlAudioPlaying, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.pre, onButtonPreNotificationClick());
            remoteViews.setOnClickPendingIntent(R.id.play, onButtonPlayNotificationClick());
            remoteViews.setOnClickPendingIntent(R.id.next, onButtonNextNotificationClick());
            remoteViews.setOnClickPendingIntent(R.id.close, onButtonCloseNotificationClick());
            startForeground(1, notification.build());
        }
    }

    public PendingIntent onButtonPreNotificationClick() {
        testIntent.setAction(AUDIO_ACTION);
        testIntent.putExtra(BUTTON_CLICK, R.id.pre);
        return PendingIntent.getBroadcast(this, 1, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent onButtonPlayNotificationClick() {
        testIntent.putExtra(BUTTON_CLICK, R.id.play);
        testIntent.setAction(AUDIO_ACTION);
        return PendingIntent.getBroadcast(this, 2, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent onButtonNextNotificationClick() {
        testIntent.putExtra(BUTTON_CLICK, R.id.next);
        testIntent.setAction(AUDIO_ACTION);
        return PendingIntent.getBroadcast(this, 3, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public PendingIntent onButtonCloseNotificationClick() {
        testIntent.putExtra(BUTTON_CLICK, R.id.close);
        testIntent.setAction(AUDIO_ACTION);
        return PendingIntent.getBroadcast(this, 4, testIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void playAudio(int position) {
        initMediaPlayer(listSongModels.get(position).getData());
    }

    public void initMediaPlayer(String pathAudio) {
        if (player != null) {
            player.stop();
        }
        player = new MediaPlayer();
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(pathAudio);
            player.setOnPreparedListener(onPreparedListener);
            player.setOnCompletionListener(onCompletionListener);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener onPreparedListener = mediaPlayer -> mediaPlayer.start();
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (playType == 0) {
                if (positionSong < models.size() - 1) {
                    positionSong = positionSong + 1;
                } else {
                    positionSong = 0;
                }
                if (imgPlay != null) {
                    audioActivity.updateText(listSongModels.get(positionSong));
                    imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_trending_flat));
                }
                if (tvSN != null) {
                    tvSN.setText(models.get(positionSong).getSongName());
                }
                updateNotification(models.get(positionSong), false, positionSong);
                initMediaPlayer(models.get(positionSong).getData());
            } else if (playType == 1) {
                initMediaPlayer(models.get(positionSong).getData());
            } else {
                Random random = new Random();
                positionSong = random.nextInt(listSongModels.size());
                if (imgPlay != null) {
                    audioActivity.updateText(listSongModels.get(positionSong));
                    imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_shuffle));
                }
                if (tvSN != null) {
                    tvSN.setText(models.get(positionSong).getSongName());
                }
                updateNotification(models.get(positionSong), false, positionSong);
                initMediaPlayer(models.get(positionSong).getData());
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicBroadcast);
    }

    public void updateNotification(SongModel model, boolean isPause, int pos) {
        if (remoteViews != null) {
            if (isPause) {
                remoteViews.setImageViewResource(R.id.play, R.drawable.ic_play_black);
            } else {
                remoteViews.setImageViewResource(R.id.play, R.drawable.ic_pause_black);
            }
            remoteViews.setTextViewText(R.id.songName, model.getSongName());
            notificationIntent.putExtra("position", positionSong);
            notificationIntent.putExtra("duration", player.getDuration());
            notification.setCustomContentView(remoteViews);
            notificationManager.notify(1, notification.build());
        }
    }

    public void preAudio() {
        if (positionSong > 0) {
            positionSong--;
        } else {
            positionSong = listSongModels.size() - 1;
        }
        notificationIntent.putExtra("position", positionSong);
        notificationIntent.putExtra("duration", player.getDuration());
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        initMediaPlayer(listSongModels.get(positionSong).getData());
        updateNotification(listSongModels.get(positionSong), false, positionSong);
        tvSN.setText(listSongModels.get(positionSong).getSongName());
    }

    public void nextAudio() {
        if (positionSong < listSongModels.size() - 1) {
            positionSong++;
        } else {
            positionSong = 0;
        }
        notificationIntent.putExtra("position", positionSong);
        notificationIntent.putExtra("duration", player.getDuration());
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        initMediaPlayer(listSongModels.get(positionSong).getData());
        updateNotification(listSongModels.get(positionSong), false, positionSong);
        tvSN.setText(listSongModels.get(positionSong).getSongName());
    }


    public class MusicBroadcast extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(AUDIO_ACTION)) {
                int id = intent.getIntExtra(BUTTON_CLICK, 0);
                if (id == R.id.pre) {
                    preAudio();
                    if (imgPlay != null) {
                        audioActivity.updateText(listSongModels.get(positionSong));
                    }
                } else if (id == R.id.next) {
                    nextAudio();
                    if (imgPlay != null) {
                        audioActivity.updateText(listSongModels.get(positionSong));
                    }
                } else if (id == R.id.play) {
                    if (player.isPlaying()) {
                        updateNotification(listSongModels.get(positionSong), true, positionSong);
                        player.pause();
                        MainActivity.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
                    } else {
                        updateNotification(listSongModels.get(positionSong), false, positionSong);
                        player.start();
                        MainActivity.imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                    }
                    if (imgPlay != null) {
                        audioActivity.updateImage();
                    }
                } else if (id == R.id.close) {
                    System.exit(0);
                }
            }
        }
    }
}
