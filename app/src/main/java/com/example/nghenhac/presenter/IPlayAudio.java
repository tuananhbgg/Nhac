package com.example.nghenhac.presenter;
import com.example.nghenhac.model.SongModel;

public interface IPlayAudio {
    void HandlingAudio(boolean isPause);
    void nextAudio(SongModel model,int position);
    void preAudio(SongModel model,int position);
    void countTime(String currentTime, int currentPercentSeekbar);
    void seekToAudio(String time);
    void skipAudio(int currentTime);
    void deleteAudio();
    void handlingPlayType();
}
