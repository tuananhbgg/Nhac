package com.example.nghenhac.model;

public class SongModel{

    private String id;
    private String composer;
    private String songName;
    private String data;
    private String displayName;
    private String duration;
    private byte[] image;

    public SongModel(String id, String composer, String songName, String data, String displayName, String duration, byte[] image) {
        this.id = id;
        this.composer = composer;
        this.songName = songName;
        this.data = data;
        this.displayName = displayName;
        this.duration = duration;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
