package com.example.nghenhac.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nghenhac.PlayMusicService;
import com.example.nghenhac.R;
import com.example.nghenhac.adapter.SongAdapter;
import com.example.nghenhac.model.SongModel;
import com.example.nghenhac.presenter.IMainActivity;
import com.example.nghenhac.presenter.MainActivityPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nghenhac.PlayMusicService.player;

public class MainActivity extends AppCompatActivity implements IMainActivity {

    @BindView(R.id.rcvSongList)
    RecyclerView rcvSongList;
    @BindView(R.id.imgPre)
    ImageView imgPre;
    @BindView(R.id.imgNext)
    ImageView imgNext;
    @BindView(R.id.rlAudioPlaying)
    RelativeLayout rlAudioPlaying;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private MainActivityPresenter mainActivityPresenter;
    private SongAdapter adapter;
    public static ArrayList<SongModel> models;
    public static int positionSong = 0;
    private Intent intentService;
    private PlayMusicService playMusicService;
    public static ImageView imgPlay;
    public static TextView tvSN;
    public static ImageView imgView;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.inflateMenu(R.menu.option_menu);
        imgPlay = findViewById(R.id.imgPlay);
        tvSN = findViewById(R.id.tvSN);
        imgView = findViewById(R.id.imgView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        playMusicService = new PlayMusicService();
        rcvSongList.setLayoutManager(manager);
        mainActivityPresenter = new MainActivityPresenter(this, this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
            mainActivityPresenter.getListSongFromDevice();
        } else {
            mainActivityPresenter.requestLocationPermisstion();
        }

        initAudioPlayDefaul();

        imgPlay.setOnClickListener(view -> {
            if (player == null) {
                intentService = new Intent(MainActivity.this, PlayMusicService.class);
                imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
                ContextCompat.startForegroundService(MainActivity.this, intentService);
                mainActivityPresenter.animationImage(imgView);
            } else {
                mainActivityPresenter.HandlingAudio();
            }

        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mn_exit:
                        System.exit(0);
                        break;
                }
                return false;
            }
        });

        imgNext.setOnClickListener(view -> {
            if (player == null || player.isPlaying()) {
                mainActivityPresenter.nextAudioMain();
            }
        });

        imgPre.setOnClickListener(view -> {
            if (player == null || player.isPlaying()) {
                mainActivityPresenter.preAudioMain();
            }
        });


        rlAudioPlaying.setOnClickListener(view -> {
            if (player != null) {
                Intent intent = new Intent(MainActivity.this, PlayAudioActivity.class);
                intent.putExtra("position", positionSong);
                startActivity(intent);
            }
        });
    }

    @Override
    public void getListSongFromDevice(ArrayList<SongModel> list) {
        adapter = new SongAdapter(list, mainActivityPresenter, this);
        models = list;
        if (models != null) {
            tvSN.setText(models.get(positionSong).getSongName());
        }
        rcvSongList.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void HandlingAudio(boolean isPause) {
        if (isPause) {
            imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
        } else {
            imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
        }
        playMusicService.updateNotification(models.get(positionSong), isPause, positionSong);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void nextAudioMain(int pos) {
        tvSN.setText(models.get(pos).getSongName());
        playMusicService.updateNotification(models.get(pos), false, pos);
        if (player != null) {
            playMusicService.initMediaPlayer(models.get(pos).getData());
        }


    }

    @Override
    public void preAudioMain(int pos) {
        tvSN.setText(models.get(pos).getSongName());
        playMusicService.updateNotification(models.get(pos), false, pos);
        if (player != null) {
            playMusicService.initMediaPlayer(models.get(pos).getData());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void audioClick(SongModel model) {
        tvSN.setText(model.getSongName());
        imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
    }

    private void initAudioPlayDefaul() {
        if (models != null) {
            tvSN.setText(models.get(0).getSongName());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mainActivityPresenter.getListSongFromDevice();
            rcvSongList.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Không có quyền truy cập bộ nhớ", Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onRestart() {
        super.onRestart();
        mainActivityPresenter.getListSongFromDevice();
        adapter.notifyDataSetChanged();
        if (player != null) {
            tvSN.setText(models.get(positionSong).getSongName());
            if (player.isPlaying()) {
                imgPlay.setImageDrawable(getDrawable(R.drawable.ic_pause));
            } else {
                imgPlay.setImageDrawable(getDrawable(R.drawable.ic_play));
                imgView.clearAnimation();
            }
        }
    }
}
