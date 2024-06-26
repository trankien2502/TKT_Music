package com.example.music.model;

import java.io.Serializable;

public class Song implements Serializable {

    private int id;
    private String title;
    private String image;
    private String url;
    private String artist;
    private String lyric;

    private boolean latest;
    private boolean featured;
    private int count;
    private boolean isPlaying;

    public Song() {
    }

    public Song(int id, String title, String image, String url, String artist, String lyric, boolean latest, boolean featured, int count) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.url = url;
        this.artist = artist;
        this.lyric = lyric;
        this.latest = latest;
        this.featured = featured;
        this.count = count;
    }

    public Song(int id, String title, String image, String url, String artist, boolean latest, boolean featured, int count) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.url = url;
        this.artist = artist;
        this.latest = latest;
        this.featured = featured;
        this.count = count;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
