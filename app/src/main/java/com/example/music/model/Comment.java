package com.example.music.model;

public class Comment {
    private int id;
    private int songid;
    private int userid;
    private String username;
    private String comment;
    private boolean ownercomment;

    public Comment() {
    }

    public Comment(int id, int songid, int userid,String username, String comment) {
        this.id = id;
        this.songid = songid;
        this.username = username;
        this.userid = userid;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isOwnercomment() {
        return ownercomment;
    }

    public void setOwnercomment(boolean ownercomment) {
        this.ownercomment = ownercomment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSongid() {
        return songid;
    }

    public void setSongid(int songid) {
        this.songid = songid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
