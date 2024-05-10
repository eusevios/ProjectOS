package Parsers;
import Entities.AnimeEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AnimeParser extends Parser{

    @Override
    void parse(String filename) throws IOException, InterruptedException, ParseException {
        int pageNumber = Integer.parseInt(Jsoup.connect("https://shikimori.one/animes/status/ongoing").
                get().getElementsByClass("link-total").last().text());
        List<AnimeEntity> animeEntities = new ArrayList<>();

        for (int i = 1; i <= pageNumber; i++) {
            Connection con = Jsoup.connect( "https://shikimori.one/animes/status/ongoing/page/"+i);
            Document doc = con.get();

            List<String> ongoingAnimeURLs = doc.getElementsByClass("cc-entries").select("a").eachAttr("href");
            for(String url : ongoingAnimeURLs){
                Document animePage = Jsoup.connect(url).get();
                AnimeEntity anime = new AnimeEntity();

                String str = animePage.select("h1").text();
                int ind = str.lastIndexOf("/");
                anime.name = (ind == -1 ? str : str.substring(ind+2));

                try {
                    anime.type = animePage.select("div.key:containsOwn(Тип:)").first().nextElementSibling().text();
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try{
                    anime.duration = animePage.select("div.key:containsOwn(Длительность эпизода:)").first().nextElementSibling().text();
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    anime.genres = animePage.select("div.key:containsOwn(Жанр)").first().nextElementSibling()
                            .select("span.genre-en").eachText().toArray(new String[0]);
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    anime.topics = animePage.select("div.key:containsOwn(Тем)").
                            first().nextElementSibling().select("span.genre-en").eachText().toArray(new String[0]);
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    anime.rating = animePage.select("div.key:containsOwn(Рейтинг)").first().nextElementSibling().text();
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    String date = animePage.select("div.key:containsOwn(Следующий эпизод:)").first().
                            nextElementSibling().getElementsByClass("local-time").
                            attr("data-datetime");
                    date = date.substring(0, date.indexOf("T"));

                    anime.date = ParsingUtils.formatDate(date, "yyyy-MM-dd", "dd.MM.yyyy");
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}


                try{
                    anime.imgURL = animePage.select("div.b-db_entry-poster.b-image").attr("data-href");
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}


                Element elem = animePage.select("img.studio-logo").first();

                if(elem!=null) {
                    str = elem.attr("alt");
                    anime.studio = str.substring(str.lastIndexOf("и") + 2);
                }
                else {
                    anime.studio = animePage.select("a.b-tag").attr("title");
                }

                animeEntities.add(anime);
                Thread.sleep(500);

            }
        }

        ParsingUtils.toSerializeJson(animeEntities, filename);

    }


}
