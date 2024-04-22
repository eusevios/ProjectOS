package Parsers;

import Entities.GameEntity;
import Entities.MovieEntity;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.EOFException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GameParser extends Parser{
    @Override
    void parse(String filename) throws IOException, InterruptedException, ParseException {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.ign.com/upcoming/games");

        WebElement button1 = driver.findElement(By.className("calendar-dropdown"));
        button1.click();

        List<String> links = new ArrayList<>();

        List<WebElement> buttons = driver.findElements(By.cssSelector("button.menu-item.jsx-1036129091"));

        for(WebElement button : buttons) {
            button.click();
            Thread.sleep(2000);
            List<WebElement> currentLinks = driver.findElements(By.cssSelector("a.tile-link"));
            for (WebElement elem : currentLinks) {
                links.add(elem.getAttribute("href"));
            }
        }
        Set<String> setLinks = new LinkedHashSet<>(links);
        driver.quit();
        List<GameEntity> list = new ArrayList<>();
        System.out.println(setLinks.size());
        for(String link : setLinks) {
            try {

                Document doc = Jsoup.connect(link).get();
                GameEntity game = new GameEntity();

                game.setName( doc.getElementsByClass("display-title jsx-1812565333 balanced").text());
                game.setPublishers( doc.select(".object-summary-text.publishers-info .small a").text());
                game.setDevelopers( doc.select(".object-summary-text.developers-info .small a").text());
                game.setFranchises( doc.select(".object-summary-text.franchises-info .small a").text());
                game.setGenres(doc.select(".object-summary-text.genres-info .small a").eachText().toArray(new String[0]));
                game.setPlatforms(doc.select(".object-summary-text.platforms-info .platforms a").eachAttr("title").toArray(new String[0]));
                game.setSummary(doc.select(".object-summary-text.summary-info .interface").text());

                String date = doc.getElementsByAttributeValue("data-cy", "release-date").text();
                if (date.contains(","))
                    game.setDate(ParsingUtils.formatDate(date, "MMM dd, yyyy", "dd.MM.yyyy"));
                else {
                    try {
                        game.setDate(ParsingUtils.formatDate(date, "MMMM yyyy", "MM.yyyy"));
                    }
                    catch (ParseException e){
                        game.setDate(date);
                    }
                }
                game.setImgURL(doc.getElementsByClass("jsx-2982775576 object-thumbnail").select("img").attr("src"));

                list.add(game);
            }
            catch (HttpStatusException | EOFException ignored){};
        }
        ParsingUtils.toSerializeJson(list, filename);
    }
}
