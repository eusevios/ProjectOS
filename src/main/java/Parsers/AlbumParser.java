package Parsers;
import Entities.AlbumEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.print.Doc;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlbumParser extends  Parser {

    private static final Logger logger  = LogManager.getLogger(AlbumParser.class);
    @Override
    public void parse() throws IOException {

        logger.info("Parsing of albums started");

        int pageNumber = Integer.parseInt(Jsoup.connect("https://www.last.fm/music/+releases/coming-soon/popular").
                get().getElementsByClass("pagination-page").last().text());

        List<AlbumEntity> albums = new ArrayList<>();

        Document doc;

        for (int i = 1; i <= pageNumber; i++) {

            try {
                doc = Jsoup.connect("https://www.last.fm/music/+releases/coming-soon/popular?page=" + i).get();
            }
            catch (Exception e){
                logger.info("Connection error: page " + i, e);
                continue;
            }

            List<String> upcomingAlbumsURLs = doc.getElementsByClass("resource-list--release-list-item-name").select("a").eachAttr("href");

            for (String url : upcomingAlbumsURLs) {
                AlbumEntity album = new AlbumEntity();
                String lfm = "https://www.last.fm"+url;
                Document albumPage;
                try{
                    albumPage = Jsoup.connect(lfm).get();
                }
                catch (Exception e){
                    logger.info("Connection error: " + lfm, e);
                    continue;
                }

                try {
                    album.setName(albumPage.select("h1").text());
                    album.setAuthor(albumPage.select(".about-artist-name").first().text());
                    album.setDate(ParsingUtils.formatDate(albumPage.getElementsByClass("catalogue-metadata-description").last().text(), "d MMMM yyyy", "dd.MM.yyyy"));
                    album.setImgURL(albumPage.select("div.header-new-background-image").attr("content"));
                    album.setTrackList(albumPage.getElementsByClass("chartlist-name").eachText().toArray(new String[0]));
                    album.setTags(Jsoup.connect(lfm + "/+tags").get().getElementsByClass("big-tags-item-name").eachText().toArray(new String[0]));
                    albums.add(album);
                }
                catch (NullPointerException | ParseException e){
                    logger.info("Parsing error", e);
                }
            }
        }
        Date date = new Date();
        date.setMonth(date.getMonth()+1);
        date.setYear(date.getYear() + 1900);
        String filename = "albums_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getDate() + "_" + date.getMonth() + "_" + date.getYear() + ".json";
        ParsingUtils.toSerializeJson(albums, filename);
    }

}
