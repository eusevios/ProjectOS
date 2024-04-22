package Parsers;

import Entities.AlbumEntity;
import Entities.AnimeEntity;
import Entities.Entity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
            System.out.println(3);
            Document doc = Jsoup.connect( "https://shikimori.one/animes/status/ongoing/page/"+i).get();
            List<String> ongoingAnimeURLs = doc.getElementsByClass("cc-entries").select("a").eachAttr("href");
            for(String url : ongoingAnimeURLs){
                Document animePage = Jsoup.connect(url).get();
                AnimeEntity anime = new AnimeEntity();
                try {
                    anime.name = animePage.select("h1").text();
                    anime.type = animePage.select("div.key:containsOwn(Тип:)").first().nextElementSibling().text();

                    String date = animePage.select("div.key:containsOwn(Следующий эпизод:)").first().
                            nextElementSibling().getElementsByClass("local-time").
                            attr("data-datetime");
                    anime.date = date.substring(0, date.indexOf("T"));
                    anime.duration = animePage.select("div.key:containsOwn(Длительность эпизода:)").first().nextElementSibling().text();
                    anime.genres = animePage.select("div.key:containsOwn(Жанр)").first().nextElementSibling()
                            .select("span.genre-en").eachText().toArray(new String[0]);
                    System.out.println(anime.genres[0]);
                    anime.topics =  animePage.select("div.key:containsOwn(Тем)").
                            first().nextElementSibling().select("span.genre-en").eachText().toArray(new String[0]);
                    anime.rating = animePage.select("div.key:containsOwn(Рейтинг)").first().nextElementSibling().text();


                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}
                System.out.println(anime.name);
                System.out.println(anime.type);
                System.out.println(anime.date);
                System.out.println(anime.duration);
                System.out.println(anime.rating);

            }
        }
    }


}
