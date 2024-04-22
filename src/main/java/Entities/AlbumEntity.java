package Entities;

public class AlbumEntity extends Entity {
    private String name;
    private String author;
    private String date;
    private String imgURL;
    private String[] tags;
    private String[] trackList;

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String[] getTags() {
        return tags;
    }

    public String[] getTrackList() {
        return trackList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setTrackList(String[] trackList) {
        this.trackList = trackList;
    }
}