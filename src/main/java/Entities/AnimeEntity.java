package Entities;

public class AnimeEntity extends Entity {

    private String name;
    private String type;
    private String date;
    private String duration;
    private String[] genres;
    private String[] topics;
    private String rating;
    private String studio;
    private String imgURL;
    private String summary;

    public void setName(String name) {
        this.name = name;
    }


    public void setType(String type) {
        this.type = type;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }


    public void setTopics(String[] topics) {
        this.topics = topics;
    }


    public void setRating(String rating) {
        this.rating = rating;
    }


    public void setStudio(String studio) {
        this.studio = studio;
    }


    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }


    public void setSummary(String summary) {
        this.summary = summary;
    }
}
