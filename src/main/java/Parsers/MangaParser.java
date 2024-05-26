package Parsers;

import Entities.MangaEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MangaParser extends Parser {
    @Override
    void parse() throws IOException, InterruptedException {

        int pageNumber = Integer.parseInt(Jsoup.connect("https://shikimori.one/mangas/status/ongoing").
                get().getElementsByClass("link-total").last().text());
        List<MangaEntity> mangaEntities = new ArrayList<>();
        for (int i = 1; i <= pageNumber; i++) {
            Connection con = Jsoup.connect("https://shikimori.one/mangas/status/ongoing/page/" + i);
            Document doc = con.get();

            List<String> ongoingAnimeURLs = doc.getElementsByClass("cc-entries").select("a").eachAttr("href");
            for(String url : ongoingAnimeURLs) {

                Document mangaPage = Jsoup.connect(url).get();
                MangaEntity manga = new MangaEntity();

                String str = mangaPage.select("h1").text();
                int ind = str.lastIndexOf("/");
                manga.setName((ind == -1 ? str : str.substring(ind + 2)));

                try {
                    manga.setGenres(mangaPage.select("div.key:containsOwn(Жанр)").first().nextElementSibling()
                            .select("span.genre-en").eachText().toArray(new String[0]));
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                try {
                    manga.setTopics(mangaPage.select("div.key:containsOwn(Тем)").
                            first().nextElementSibling().select("span.genre-en").eachText().toArray(new String[0]));
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}


                try {
                    System.out.println(manga.name);
                    str = mangaPage.select("a.b-tag").attr("title");
                    manga.setPublisher(str.substring(str.lastIndexOf("я") + 2));
                }
                catch (Selector.SelectorParseException | NullPointerException | StringIndexOutOfBoundsException ignored){}

                try{
                    manga.setImgURL(mangaPage.select("div.b-db_entry-poster.b-image").attr("data-href"));
                }
                catch (Selector.SelectorParseException | NullPointerException ignored){}

                manga.setSummary(mangaPage.select("div.b-text_with_paragraphs").text());

                mangaEntities.add(manga);

                Thread.sleep(500);

                Date date = new Date();
                date.setMonth(date.getMonth()+1);
                date.setYear(date.getYear() + 1900);
                String filename = "manga_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getDate() + "_" + date.getMonth() + "_" + date.getYear() + ".json";

                ParsingUtils.toSerializeJson(mangaEntities, filename);

            }
        }

    }
}
