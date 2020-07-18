package com.example.nghenhac.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.nghenhac.PlayMusicService;
import com.example.nghenhac.R;
import com.example.nghenhac.model.SongModel;
import com.example.nghenhac.presenter.IPlayAudio;
import com.example.nghenhac.presenter.PlayAudioPreSenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nghenhac.PlayMusicService.context;
import static com.example.nghenhac.PlayMusicService.player;
import static com.example.nghenhac.view.MainActivity.positionSong;

public class PlayAudioActivity extends AppCompatActivity implements IPlayAudio {

    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.imgNext)
    ImageView imgNext;
    @BindView(R.id.imgPre)
    ImageView imgPre;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.imgMore)
    ImageView imgMore;
    @BindView(R.id.imgSkipBack)
    ImageView imgSkipBack;
    @BindView(R.id.imgSkipAhead)
    ImageView imgSkipAhead;
    @BindView(R.id.cardview)
    CardView cardview;

    private ArrayList<SongModel> models;
    private int position;
    private PlayAudioPreSenter playAudioPreSenter;
    private int duration;
    public static ImageView imgPlay, imgView, imgPlayType;
    public static TextView tvSongName, tvComposer, tvCurrentTime, tvTotalTime;
    private PlayMusicService playMusicService;
    private Dialog dialog;
    private TextView tvSongNameDelete, tvPositive, tvNegative;
    public static int playType = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);
        ButterKnife.bind(this);
        imgPlay = findViewById(R.id.imgPlay);
        imgView = findViewById(R.id.imgView);
        imgPlayType = findViewById(R.id.imgPlayType);
        tvSongName = findViewById(R.id.tvSongName);
        tvComposer = findViewById(R.id.tvComposer);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        playMusicService = new PlayMusicService();
        models = new ArrayList<>();
        playAudioPreSenter = new PlayAudioPreSenter(this);
        if (getIntent() != null) {
            models = MainActivity.models;
            position = getIntent().getIntExtra("position", 0);
            duration = Integer.parseInt(models.get(position).getDuration());
            tvSongName.setText(models.get(position).getSongName());
            tvComposer.setText(models.get(position).getComposer());
            tvTotalTime.setText(playAudioPreSenter.convertMiliSecondToMinute(duration));
            if (models.get(positionSong).getImage() != null) {
                Glide.with(PlayAudioActivity.this).load(models.get(positionSong).getImage()).into(imgView);
            }
        }
        if (player != null) {
            if (player.isPlaying()) {
                //playAudioPreSenter.animationImage(cardview);
                playAudioPreSenter.countTime();
            } else {
                tvCurrentTime.setText(playAudioPreSenter.convertMiliSecondToMinute(player.getCurrentPosition()));
                seekbar.setProgress(100 * player.getCurrentPosition() / player.getDuration());
                imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
            }
        } else {
            imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
        }

        imgPlay.setOnClickListener(view -> {
            if (player != null) {
                playAudioPreSenter.HandlingAudio();
            } else {
                Intent intentService = new Intent(PlayAudioActivity.this, PlayMusicService.class);
                imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                ContextCompat.startForegroundService(PlayAudioActivity.this, intentService);
            }
        });
        imgNext.setOnClickListener(view -> {
            if (player == null || player.isPlaying()) {
                seekbar.setProgress(0);
                tvCurrentTime.setText("0:00");
                playAudioPreSenter.nextAudio(models, position);
                playAudioPreSenter.countTime();
            }

        });
        imgPre.setOnClickListener(view -> {
            if (player == null || player.isPlaying()) {
                seekbar.setProgress(0);
                tvCurrentTime.setText("0:00");
                playAudioPreSenter.preAudio(models, position);
                playAudioPreSenter.countTime();
            }

        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playAudioPreSenter.seekToAudio(seekBar);
            }
        });
        imgBack.setOnClickListener(view -> finish());

        imgMore.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(PlayAudioActivity.this, view);
            getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.popup_mn_delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_music, null);
                        tvSongNameDelete = dialogView.findViewById(R.id.tvSongNameDelete);
                        tvPositive = dialogView.findViewById(R.id.tvPositive);
                        tvNegative = dialogView.findViewById(R.id.tvNegative);
                        builder.setView(dialogView);
                        tvSongNameDelete.setText(models.get(positionSong).getSongName());
                        tvPositive.setOnClickListener(view1 -> {
                            playAudioPreSenter.deleteAudio(models.get(positionSong), PlayAudioActivity.this);
                            dialog.dismiss();
                        });
                        tvNegative.setOnClickListener(view12 -> dialog.dismiss());
                        dialog = builder.create();
                        dialog.show();
                        break;
                    case R.id.popup_mn_rington:
                        break;
                }
                return true;
            });
            popupMenu.show();
        });

        imgSkipAhead.setOnClickListener(view -> playAudioPreSenter.skipAhead());
        imgSkipBack.setOnClickListener(view -> playAudioPreSenter.skipBack());
        imgPlayType.setOnClickListener(view -> playAudioPreSenter.handlingPlayType());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void HandlingAudio(boolean isPause) {
        if (isPause) {
            imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
            cardview.clearAnimation();
        } else {
            imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
            playAudioPreSenter.animationImage(cardview);
        }
        playMusicService.updateNotification(models.get(position), isPause, position);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void nextAudio(SongModel model, int pos) {
        position = pos;
        tvSongName.setText(model.getSongName());
        tvComposer.setText(model.getComposer());
        tvTotalTime.setText(playAudioPreSenter.convertMiliSecondToMinute(Integer.parseInt(model.getDuration())));
        if (model.getImage() != null) {
            Glide.with(PlayAudioActivity.this).load(model.getImage()).into(imgView);
        } else {
            Glide.with(this).load(R.drawable.img_music).into(imgView);
        }
        playMusicService.updateNotification(model, false, pos);
        playMusicService.initMediaPlayer(model.getData());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void preAudio(SongModel model, int pos) {
        position = pos;
        tvSongName.setText(model.getSongName());
        tvComposer.setText(model.getComposer());
        tvTotalTime.setText(playAudioPreSenter.convertMiliSecondToMinute(Integer.parseInt(model.getDuration())));
        if (model.getImage() != null) {
            Glide.with(PlayAudioActivity.this).load(model.getImage()).into(imgView);
        } else {
            Glide.with(this).load(R.drawable.img_music).into(imgView);
        }
        playMusicService.updateNotification(model, false, pos);
        playMusicService.initMediaPlayer(model.getData());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void countTime(String currentTime, int currentPercentSeekbar) {
        tvCurrentTime.setText(currentTime);
        seekbar.setProgress(currentPercentSeekbar);
        switch (playType) {
            case 0:
                imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_trending_flat));
                break;
            case 1:
                imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_repeat));
                break;
            case 2:
                imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_shuffle));
                break;
        }
    }

    @Override
    public void seekToAudio(String time) {
        tvCurrentTime.setText(time);
    }

    @Override
    public void skipAudio(int current) {
        tvCurrentTime.setText(playAudioPreSenter.convertMiliSecondToMinute(player.getCurrentPosition()));
    }

    @Override
    public void deleteAudio() {
        tvSongName.setText(models.get(positionSong).getSongName());
        tvComposer.setText(models.get(positionSong).getComposer());
        tvTotalTime.setText(playAudioPreSenter.convertMiliSecondToMinute(Integer.parseInt(models.get(positionSong).getDuration())));
        if (models.get(positionSong).getImage() != null) {
            Glide.with(PlayAudioActivity.this).load(models.get(positionSong)).into(imgView);
        } else {
            Glide.with(this).load(R.drawable.img_music).into(imgView);
        }
        playMusicService.updateNotification(models.get(positionSong), false, positionSong);
        playMusicService.initMediaPlayer(models.get(positionSong).getData());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handlingPlayType() {
        switch (playType) {
            case 0:
                Toast.makeText(PlayAudioActivity.this, "Tắt phát ngẫu nhiên", Toast.LENGTH_SHORT).show();
                imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_trending_flat));
                break;
            case 1:
                Toast.makeText(PlayAudioActivity.this, "Lặp lại một bài hát", Toast.LENGTH_SHORT).show();
                imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_repeat));
                break;
            case 2:
                Toast.makeText(PlayAudioActivity.this, "Phát ngẫu nhiên", Toast.LENGTH_SHORT).show();
                imgPlayType.setImageDrawable(getDrawable(R.drawable.ic_shuffle));
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateImage() {
        if (player.isPlaying()) {
            imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
        } else {
            imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        playAudioPreSenter.animationImage(cardview);
    }

    @Override
    protected void onRestart() {
        if (player != null) {
            tvSongName.setText(PlayMusicService.listSongModels.get(positionSong).getSongName());
            tvComposer.setText(PlayMusicService.listSongModels.get(positionSong).getComposer());
            tvTotalTime.setText(convertMiliSecondToMinute(player.getDuration()));
            if (models.get(positionSong).getImage() != null) {
                Glide.with(PlayAudioActivity.this).load(models.get(positionSong)).into(imgView);
            } else {
                Glide.with(this).load(R.drawable.img_music).into(imgView);
            }
        }
        super.onRestart();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void updateText(SongModel model) {
        tvSongName.setText(model.getSongName());
        tvComposer.setText(model.getComposer());
        tvTotalTime.setText(convertMiliSecondToMinute(Integer.parseInt(model.getDuration())));


        if (model.getImage() != null) {
            Glide.with(context).load(model.getImage()).into(imgView);
        } else {
            Glide.with(context).load(R.drawable.img_music).into(imgView);
        }
        PlayAudioPreSenter.currentTime = 0;
        PlayAudioPreSenter.minute = 0;
    }

    public String convertMiliSecondToMinute(int miliSecond) {
        String duration;
        int minute = miliSecond / 60000;
        String second = (miliSecond % 60000) / 1000 + "";
        if (Integer.parseInt(second) < 10) {
            duration = minute + ":0" + second;
        } else {
            duration = minute + ":" + second;
        }
        return duration;
    }
}