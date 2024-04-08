package Entities;

public class AlbumEntity extends Entity {
    public String name;
    public String author;
    public String date;
    public String imgURL;
    public String[] tags;
    public String[] trackList;
    public AlbumEntity(){
        name = "";
        author = "";
        date = "";
        imgURL = "";
    }

}