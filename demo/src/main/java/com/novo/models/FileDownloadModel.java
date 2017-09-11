package com.novo.models;

import java.io.Serializable;

/**
 * Created by Hisham on 11/Sep/2017 - 16:25
 */

public class FileDownloadModel implements Serializable {

    public enum Status {
        DOWNLOADING, DOWNLOADED, UNZIPPING, UNZIPPED, ERROR
    }

    private String videoId;
    private String videoTitle;
    private String link;
    private String token;
    private String filePath;
    private String targetDirectoryPath;
    private String callBackIntent;
    private int progress = -1;
    /**
     * status like unzipping, downloading... etc
     */
    private Status status;

    public Status getStatus() {
        return status;
    }

    public FileDownloadModel setStatus(Status status) {
        this.status = status;
        return this;
    }

    public int getProgress() {
        return progress;
    }

    public FileDownloadModel setProgress(int progress) {
        this.progress = progress;
        return this;
    }

    public String getVideoId() {
        return videoId;
    }

    public FileDownloadModel setVideoId(String videoId) {
        this.videoId = videoId;
        return this;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public FileDownloadModel setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
        return this;
    }

    public String getLink() {
        return link;
    }

    public FileDownloadModel setLink(String link) {
        this.link = link;
        return this;
    }

    public String getToken() {
        return token;
    }

    public FileDownloadModel setToken(String token) {
        this.token = token;
        return this;
    }

    public String getFilePath() {
        return filePath;
    }

    public FileDownloadModel setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getTargetDirectoryPath() {
        return targetDirectoryPath;
    }

    public FileDownloadModel setTargetDirectoryPath(String targetDirectoryPath) {
        this.targetDirectoryPath = targetDirectoryPath;
        return this;
    }

    public String getCallBackIntent() {
        return callBackIntent;
    }

    public FileDownloadModel setCallBackIntent(String callBackIntent) {
        this.callBackIntent = callBackIntent;
        return this;
    }
}
