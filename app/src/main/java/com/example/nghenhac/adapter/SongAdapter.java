package com.example.nghenhac.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nghenhac.PlayMusicService;
import com.example.nghenhac.R;
import com.example.nghenhac.model.SongModel;
import com.example.nghenhac.presenter.MainActivityPresenter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.nghenhac.view.MainActivity.imgView;
import static com.example.nghenhac.view.MainActivity.positionSong;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHoder> {


    private ArrayList<SongModel> models;
    private MainActivityPresenter presenter;
    private Context context;
    private Intent intent;

    public ArrayList<SongModel> getModels() {
        return models;
    }

    public void setModels(ArrayList<SongModel> models) {
        this.models = models;
    }

    public SongAdapter(ArrayList<SongModel> models, MainActivityPresenter presenter, Context context) {
        this.models = models;
        this.presenter = presenter;
        this.context = context;

    }

    @NonNull
    @Override
    public SongViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, null);
        intent = new Intent(context, PlayMusicService.class);
        return new SongViewHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHoder holder, int position) {
        SongModel model = models.get(position);
        holder.tvSongName.setText(model.getSongName());
        holder.tvComposer.setText(model.getComposer());
        if (model.getImage() != null) {
            Glide.with(context).load(model.getImage()).into(holder.imgMusic);
        } else {
            Glide.with(context).load(R.drawable.img_music).into(holder.imgMusic);
        }

        holder.itemView.setOnClickListener(view -> {
            if (PlayMusicService.player != null) {
                PlayMusicService.player.stop();
                context.stopService(intent);
                positionSong = position;
                ContextCompat.startForegroundService(context, intent);

            } else {
                positionSong = position;
                ContextCompat.startForegroundService(context, intent);
            }
            presenter.clickAudio(models.get(position));
            presenter.animationImage(imgView);
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    class SongViewHoder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSongName)
        TextView tvSongName;
        @BindView(R.id.tvComposer)
        TextView tvComposer;
        @BindView(R.id.imgMusic)
        ImageView imgMusic;

        public SongViewHoder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

