package Parsers;
import Entities.AnimeEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnimeParser extends Parser{
    private static final Logger logger  = LogManager.getLogger(AnimeParser.class);
    @Override
    void parse() throws IOException, InterruptedException, ParseException {
        logger.info("Parsing of anime started");

        int pageNumber = Integer.parseInt(Jsoup.connect("https://shikimori.one/animes/status/ongoing").
                get().getElementsByClass("link-total").last().text());
        List<AnimeEntity> animeEntities = new ArrayList<>();

        for (int i = 1; i <= pageNumber; i++) {
            Connection con = Jsoup.connect( "https://shikimori.one/animes/status/ongoing/page/"+i);
            Document doc = con.get();
            List<String> ongoingAnimeURLs = doc.getElementsByClass("cc-entries").select("a").eachAttr("href");
            Document animePage;
            for(String url : ongoingAnimeURLs){
                try {
                    animePage = Jsoup.connect(url).get();
                }
                catch (Exception e){

                    logger.info("Connection error: " + url, e);
                    continue;

                }
                AnimeEntity anime = new AnimeEntity();

                String str = animePage.select("h1").text();
                int ind = str.lastIndexOf("/");
                anime.setName((ind == -1 ? str : str.substring(ind + 2)));

                try {
                    anime.setType(animePage.select("div.key:containsOwn(Тип:)").first().nextElementSibling().text());
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try{
                    anime.setDuration(animePage.select("div.key:containsOwn(Длительность эпизода:)").first().nextElementSibling().text());
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    anime.setGenres(animePage.select("div.key:containsOwn(Жанр)").first().nextElementSibling()
                            .select("span.genre-en").eachText().toArray(new String[0]));
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    anime.setTopics(animePage.select("div.key:containsOwn(Тем)").
                            first().nextElementSibling().select("span.genre-en").eachText().toArray(new String[0]));
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    anime.setRating(animePage.select("div.key:containsOwn(Рейтинг)").first().nextElementSibling().text());
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    String date = animePage.select("div.key:containsOwn(Следующий эпизод:)").first().
                            nextElementSibling().getElementsByClass("local-time").
                            attr("data-datetime");
                    date = date.substring(0, date.indexOf("T"));

                    anime.setDate(ParsingUtils.formatDate(date, "yyyy-MM-dd", "dd.MM.yyyy"));
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}


                try{
                    anime.setImgURL(animePage.select("div.b-db_entry-poster.b-image").attr("data-href"));
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                Element elem = animePage.select("img.studio-logo").first();

                try {
                    if (elem != null) {
                        str = elem.attr("alt");
                        anime.setStudio(str.substring(str.lastIndexOf("и") + 2));
                    } else {
                        str = animePage.select("a.b-tag").attr("title");
                        anime.setStudio(str.substring(str.lastIndexOf("и") + 2));
                    }
                    anime.setSummary(animePage.select("div.b-text_with_paragraphs").text());
                }
                catch (Exception ignored){}

                animeEntities.add(anime);
                Thread.sleep(500);
            }
        }
        Date date = new Date();
        date.setMonth(date.getMonth()+1);
        date.setYear(date.getYear() + 1900);
        String filename = "animes_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getDate() + "_" + date.getMonth() + "_" + date.getYear() + ".json";
        ParsingUtils.toSerializeJson(animeEntities, filename);

    }


}
