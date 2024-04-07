package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ParserAlbums {
    private void toJson(List<Album> albums,  String filename){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(albums, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String formatDate(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
            return outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void parse(String filename) throws IOException {

        Document doc = Jsoup.connect("https://www.last.fm/music/+releases/coming-soon/popular").get();

        List<String> upcomingAlbumsURLs = doc.getElementsByClass("resource-list--release-list-item-name").select("a").eachAttr("href");

        List<Album> albums = new ArrayList<>();

        for (String url : upcomingAlbumsURLs){

            Album album = new Album();

            Document albumPage = Jsoup.connect("https://www.last.fm"+url).get();

            album.name = albumPage.select("h1").text();
            album.author = albumPage.select(".about-artist-name").first().text();
            album.date = formatDate(albumPage.getElementsByClass("catalogue-metadata-description").last().text());
            album.imgURL = albumPage.select("div.header-new-background-image").attr("content");

            try {
                album.genre = albumPage.select("li.tag").first().text();
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            albums.add(album);

        }

        toJson(albums, filename);
    }
    public class Album {
        String name;
        String author;
        String date;
        String imgURL;
        String genre;

    }

}
