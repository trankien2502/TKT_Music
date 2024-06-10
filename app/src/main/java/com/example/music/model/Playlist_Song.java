package com.example.music.model;

import java.io.Serializable;

public class Playlist_Song implements Serializable {
    private int id;
    private int pid;
    private int sid;

    public Playlist_Song() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }
}
