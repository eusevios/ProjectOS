package Entities;

public class MangaEntity extends Entity {
    public String name;
    private String[] genres;
    private String summary;
    private String publisher;
    private String[] topics;
    private String imgURL;

    public void setName(String name) {
        this.name = name;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
