package com.example.nghenhac.presenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.example.nghenhac.R;
import com.example.nghenhac.model.SongModel;

import java.io.File;
import java.util.ArrayList;

import static com.example.nghenhac.PlayMusicService.listSongModels;
import static com.example.nghenhac.PlayMusicService.player;
import static com.example.nghenhac.view.MainActivity.positionSong;
import static com.example.nghenhac.view.PlayAudioActivity.playType;

public class PlayAudioPreSenter {
    private IPlayAudio iPlayAudio;
    private int totalTimer;
    public static int currentTime = 0;
    private int count;
    public static int minute = 0;

    private Handler handler = new Handler();

    public PlayAudioPreSenter(IPlayAudio iPlayAudio) {
        this.iPlayAudio = iPlayAudio;
    }

    public String convertMiliSecondToMinute(int miliSecond) {
        String duration = "";
        int minute = miliSecond / 60000;
        String second = (miliSecond % 60000) / 1000 + "";
        if (Integer.parseInt(second) < 10) {
            duration = minute + ":0" + second;
        } else {
            duration = minute + ":" + second;
        }
        return duration;
    }

    public void nextAudio(ArrayList<SongModel> models, int position) {
        handler.removeCallbacksAndMessages(null);
        minute = 0;
        currentTime = 0;
        count = 0;
        positionSong = position;
        if (player.isPlaying()) {
            player.stop();
        }
        if (positionSong < models.size() - 1) {
            positionSong++;
        } else {
            positionSong = 0;
        }
        iPlayAudio.nextAudio(models.get(positionSong), positionSong);
    }

    public void preAudio(ArrayList<SongModel> models, int position) {
        handler.removeCallbacksAndMessages(null);
        minute = 0;
        currentTime = 0;
        count = 0;
        positionSong = position;
        if (player.isPlaying()) {
            player.stop();
        }
        if (positionSong > 0) {
            positionSong--;

        } else {
            positionSong = models.size() - 1;
        }
        iPlayAudio.preAudio(models.get(positionSong), positionSong);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void HandlingAudio() {
        boolean isPause;
        if (player.isPlaying()) {
            player.pause();
            handler.removeCallbacks(runnable);
            isPause = true;
        } else {
            countTime();
            player.start();
            isPause = false;
        }
        iPlayAudio.HandlingAudio(isPause);
    }

    public void countTime() {
        count = (player.getCurrentPosition() % 60000) / 1000;
        minute = player.getCurrentPosition() / 60000;
        totalTimer = player.getDuration();
        handler.postDelayed(runnable, 1000);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String currentTimeS;
            int totalTimeSecond = totalTimer / 1000;
            if (currentTime < totalTimeSecond) {
                currentTime++;
                count++;
                if (count == 60) {
                    minute++;
                    count = 0;
                }
            }
            if (count < 10) {
                currentTimeS = minute + ":0" + count;
            } else {
                currentTimeS = minute + ":" + count;
            }
            int currentPersent = 100 * player.getCurrentPosition() / totalTimer;
            iPlayAudio.countTime(currentTimeS, currentPersent);
            countTime();
        }
    };

    public void seekToAudio(SeekBar seekBar) {
        int percent = seekBar.getProgress();
        int crTime = percent * totalTimer / 100;
        player.seekTo(crTime);
        String timeSeekToS = convertMiliSecondToMinute(player.getCurrentPosition());
        iPlayAudio.seekToAudio(timeSeekToS);
        minute = player.getCurrentPosition() / 60000;
        currentTime = player.getCurrentPosition() / 1000;
        count = (player.getCurrentPosition() % 60000) / 1000;
    }


    public void skipAhead(){
        if(player!=null){
            iPlayAudio.skipAudio(player.getCurrentPosition()+15000);
            player.seekTo(player.getCurrentPosition()+15000);

        }
    }
    public void skipBack(){
        if(player!=null){
            iPlayAudio.skipAudio(player.getCurrentPosition()+15000);
            player.seekTo(player.getCurrentPosition()-15000);
        }
    }

    public void deleteAudio(SongModel model,Context context){
        File file = new File(model.getData());
        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[]{
                file.getAbsolutePath()
        };
         ContentResolver contentResolver = context.getContentResolver();
         Uri filesUri = MediaStore.Files.getContentUri("external");
        contentResolver.delete(filesUri, where, selectionArgs);
        if (file.exists()) {
            contentResolver.delete(filesUri, where, selectionArgs);
        }
        if (positionSong < listSongModels.size()-1){
            positionSong++;
        }else {
            positionSong = 0;
        }
        iPlayAudio.deleteAudio();
    }

    public void animationImage(CardView img) {
        RotateAnimation animation = new RotateAnimation(0, 360, img.getWidth() / 2, img.getHeight() / 2);
        animation.setDuration(10000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setFillBefore(true);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        img.startAnimation(animation);
    }
    public void handlingPlayType(){
        if(playType == 2){
            playType = 0;
        }else {
            playType++;
        }
        iPlayAudio.handlingPlayType();
    }
}
