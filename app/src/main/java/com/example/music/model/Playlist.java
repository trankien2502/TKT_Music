package com.example.music.model;

import java.io.Serializable;
import java.util.List;

public class Playlist implements Serializable {
    private int id;
    private String name;
    private int user_id;
    //private List<Song> list;

    public Playlist() {
    }

    public Playlist(String name) {
        this.name = name;
    }

    public Playlist(String name, int user_id) {
        this.name = name;
        this.user_id = user_id;
    }

    public Playlist(int id, String name, int user_id) {
        this.id = id;
        this.name = name;
        this.user_id = user_id;
    }
//    public List<Song> getList() {
//        return list;
//    }
//
//    public void setList(List<Song> list) {
//        this.list = list;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }


}
