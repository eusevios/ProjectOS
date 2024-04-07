package org.example;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(outputFormatter);
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
