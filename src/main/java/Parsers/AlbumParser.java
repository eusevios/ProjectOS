package Parsers;
import Entities.AlbumEntity;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AlbumParser extends  Parser {
    @Override
    public void parse(String filename) throws IOException {

        int pageNumber = Integer.parseInt(Jsoup.connect("https://www.last.fm/music/+releases/coming-soon/popular").
                get().getElementsByClass("pagination-page").last().text());

        List<AlbumEntity> albums = new ArrayList<>();

        int k = 0;
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
                    album.date = ParsingUtils.formatDate(albumPage.getElementsByClass("catalogue-metadata-description").last().text(), "d MMMM yyyy", "dd.MM.yyyy");
                    album.imgURL = albumPage.select("div.header-new-background-image").attr("content");
                    album.trackList = albumPage.getElementsByClass("chartlist-name").eachText().toArray(new String[0]);
                    album.tags = Jsoup.connect(lfm+"/+tags").get().getElementsByClass("big-tags-item-name").eachText().toArray(new String[0]);
                    albums.add(album);
                    ++k;
                    System.out.println(k);
                }
                catch (HttpStatusException | NullPointerException | ParseException ignored){}
            }
        }
        ParsingUtils.toSerializeJson(albums, filename);
    }

}
