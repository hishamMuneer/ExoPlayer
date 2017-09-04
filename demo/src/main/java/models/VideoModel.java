package models;

/**
 * Created by Hisham on 03/Sep/2017 - 17:08
 */

public class VideoModel {

    private String thumbnail;
    private String name;
    private String videoId;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
