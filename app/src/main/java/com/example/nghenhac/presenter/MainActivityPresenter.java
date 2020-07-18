package com.example.nghenhac.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.provider.MediaStore;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.nghenhac.PlayMusicService;
import com.example.nghenhac.model.SongModel;

import java.util.ArrayList;

import static com.example.nghenhac.PlayMusicService.player;
import static com.example.nghenhac.view.MainActivity.imgView;
import static com.example.nghenhac.view.MainActivity.models;
import static com.example.nghenhac.view.MainActivity.positionSong;

public class MainActivityPresenter {
    private IMainActivity iMainActivity;
    private Context context;
    private ArrayList<SongModel> songModels;
    private boolean isPause = true;

    public MainActivityPresenter(IMainActivity iMainActivity, Context context) {
        this.iMainActivity = iMainActivity;
        this.context = context;
    }

    public void getListSongFromDevice() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION

        };

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        ArrayList<SongModel> songs = new ArrayList<>();
        while (cursor.moveToNext()) {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(cursor.getString(3));
            byte[] bytes = mmr.getEmbeddedPicture();
            songs.add(new SongModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), bytes));
        }
        songModels = songs;
        iMainActivity.getListSongFromDevice(songs);
    }

    public void requestLocationPermisstion() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setMessage("Cho phép lấy Bộ nhớ")
                    .setPositiveButton("Ok", (dialogInterface, i) -> ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100))
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void HandlingAudio() {
        if (PlayMusicService.player.isPlaying()) {
            PlayMusicService.player.pause();
            isPause = true;
            imgView.clearAnimation();
        } else {
            PlayMusicService.player.start();
            isPause = false;
            animationImage(imgView);
        }
        iMainActivity.HandlingAudio(isPause);

    }

    public void animationImage(ImageView img) {
        RotateAnimation animation = new RotateAnimation(0, 360, img.getWidth() / 2, img.getHeight() / 2);
        animation.setDuration(6000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setFillBefore(true);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        img.startAnimation(animation);
    }

    public void nextAudioMain() {
        if (player != null) {
            player.stop();
        }

        if (positionSong < models.size() - 1) {
            positionSong++;
        } else {
            positionSong = 0;
        }
        iMainActivity.nextAudioMain(positionSong);
    }

    public void preAudioMain() {
        if (player != null) {
            player.stop();
        }
        if (positionSong > 0) {
            positionSong--;
        } else {
            positionSong = models.size() - 1;
        }
        iMainActivity.preAudioMain(positionSong);
    }

    public void clickAudio(SongModel model) {
        iMainActivity.audioClick(model);
    }
}
