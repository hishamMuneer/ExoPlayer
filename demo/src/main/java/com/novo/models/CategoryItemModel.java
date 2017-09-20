package com.novo.models;

/**
 * Created by ayushgarg on 15/09/17.
 */

public class CategoryItemModel {

    private String videoUrl;
    private String videoTitle;

    public CategoryItemModel(String videoUrl, String videoTitle) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public CategoryItemModel setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
        return this;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public CategoryItemModel setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
        return this;
    }
}
