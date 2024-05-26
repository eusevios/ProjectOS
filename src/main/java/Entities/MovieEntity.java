package Entities;

public class MovieEntity extends Entity {
    private String name;
    private String[] producers;
    private String date;
    private String[] genres;
    private String summary;
    private String imgURL;


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
