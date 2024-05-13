package Entities;

public class GameEntity extends Entity{
    private String name;
    private String publishers;
    private String developers;
    private String franchises;
    private String summary;
    private String date;
    private String[] platforms;
    private String[] genres;
    private String imgURL;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }

    public void setDevelopers(String developers) {
        this.developers = developers;
    }

    public void setFranchises(String franchises) {
        this.franchises = franchises;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlatforms(String[] platforms) {
        this.platforms = platforms;
    }

    public void setGenres(String[] genres) {
        this.genres = genres;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }


}
