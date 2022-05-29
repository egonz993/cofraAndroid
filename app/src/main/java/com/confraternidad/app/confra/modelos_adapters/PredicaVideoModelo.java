package com.confraternidad.app.confra.modelos_adapters;

import com.confraternidad.app.confra.R;

public class PredicaVideoModelo {
    private String uid, date, title, description, link;
    private int imgVideo;

    public PredicaVideoModelo(String uid, String date, String title, String description, String link) {
        this.uid = uid;
        this.date = date;
        this.title = title;
        this.description = description;
        this.link = link;
        this.imgVideo= R.drawable.ic_video;
    }

    public PredicaVideoModelo() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getImgVideo() {
        return imgVideo;
    }

    public void setImgVideo(int imgVideo) {
        this.imgVideo = imgVideo;
    }
}
