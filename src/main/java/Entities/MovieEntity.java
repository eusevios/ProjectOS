package Entities;

public class MovieEntity extends Entity {
    private String name;
    private String[] producers;
    private String date;
    private String[] genres;
    private String summary;
    private String imgURL;

    public String getName() {
        return name;
    }

    public String[] getProducers() {
        return producers;
    }

    public String getDate() {
        return date;
    }

    public String[] getGenres() {
        return genres;
    }

    public String getSummary() {
        return summary;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducers(String[] producers) {
        this.producers = producers;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
