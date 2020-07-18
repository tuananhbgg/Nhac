package com.example.nghenhac.presenter;

import android.widget.ImageView;

import com.example.nghenhac.model.SongModel;

import java.util.ArrayList;

public interface IMainActivity {
    void getListSongFromDevice(ArrayList<SongModel> list);

    void HandlingAudio(boolean isPause);

    void nextAudioMain(int position);

    void preAudioMain(int position);

    void audioClick(SongModel model);
}
