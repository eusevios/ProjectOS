package Parsers;
import Entities.AlbumEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class AlbumParser {
    private void toSerializeJson(List<AlbumEntity> albums,  String filename){
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(albums, writer);
        }
        catch (IOException ignored) {

        }
    }
    private static String formatDate(String inputDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return date.format(outputFormatter);
    }
    public void parse(String filename) throws IOException {

        int pageNumber = Integer.parseInt(Jsoup.connect("https://www.last.fm/music/+releases/coming-soon/popular").
                get().getElementsByClass("pagination-page").last().text());

        List<AlbumEntity> albums = new ArrayList<>();

        for (int i = 1; i <= pageNumber; i++) {

            Document doc = Jsoup.connect("https://www.last.fm/music/+releases/coming-soon/popular?page="+ i).get();

            List<String> upcomingAlbumsURLs = doc.getElementsByClass("resource-list--release-list-item-name").select("a").eachAttr("href");

            for (String url : upcomingAlbumsURLs) {
                try {
                    AlbumEntity album = new AlbumEntity();
                    String lfm = "https://www.last.fm"+url;
                    Document albumPage = Jsoup.connect(lfm).get();
                    album.name = albumPage.select("h1").text();
                    album.author = albumPage.select(".about-artist-name").first().text();
                    album.date = formatDate(albumPage.getElementsByClass("catalogue-metadata-description").last().text());
                    album.imgURL = albumPage.select("div.header-new-background-image").attr("content");
                    album.trackList = albumPage.getElementsByClass("chartlist-name").eachText().toArray(new String[0]);
                    album.tags = Jsoup.connect(lfm+"/+tags").get().getElementsByClass("big-tags-item-name").eachText().toArray(new String[0]);
                    albums.add(album);
                }
                catch (HttpStatusException | NullPointerException ignored){

                }
            }
        }
        toSerializeJson(albums, filename);
    }

}
