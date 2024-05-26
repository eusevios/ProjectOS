package Parsers;

import Entities.GameEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class GameParser extends Parser{
    private static final Logger logger = LogManager.getLogger(GameParser.class);
    @Override
    void parse() throws IOException, InterruptedException, ParseException {

        logger.info("Parsing of games started");

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.ign.com/upcoming/games");

        WebElement button1 = driver.findElement(By.className("calendar-dropdown"));
        button1.click();

        List<String> links = new ArrayList<>();

        List<WebElement> buttons = driver.findElements(By.cssSelector("button.menu-item.jsx-1036129091"));

        int i = -1;
        Date current_date = new Date();
        int current_month = current_date.getMonth();
        for(WebElement button : buttons) {
            ++i;
            if (i < current_month) continue;
            button.click();
            Thread.sleep(2000);
            List<WebElement> currentLinks = driver.findElements(By.cssSelector("a.tile-link"));

            for (WebElement elem : currentLinks) {
                if (elem.findElement(By.cssSelector("div.interface.jsx-2563906438.jsx-2321054750.tile-meta.small")).getText().contains("/"))
                    break;
                links.add(elem.getAttribute("href"));
            }
        }
        Set<String> setLinks = new LinkedHashSet<>(links);
        driver.quit();
        List<GameEntity> list = new ArrayList<>();
        Document gamePage;
        for(String link : setLinks) {
            try {
                gamePage = Jsoup.connect(link).get();
            } catch (Exception e) {
                logger.info("Connection error: " + link, e);
                continue;
            }
            GameEntity game = new GameEntity();
            game.setName(gamePage.getElementsByClass("display-title jsx-1812565333 balanced").text());
            System.out.println(gamePage.getElementsByClass("display-title jsx-1812565333 balanced").text());
            game.setPublishers(gamePage.select(".object-summary-text.publishers-info .small a").text());
            game.setDevelopers(gamePage.select(".object-summary-text.developers-info .small a").text());
            game.setFranchises(gamePage.select(".object-summary-text.franchises-info .small a").text());
            game.setGenres(gamePage.select(".object-summary-text.genres-info .small a").eachText().toArray(new String[0]));
            game.setPlatforms(gamePage.select(".object-summary-text.platforms-info .platforms a").eachAttr("title").toArray(new String[0]));
            game.setSummary(gamePage.select(".object-summary-text.summary-info .interface").text());

            String date = gamePage.getElementsByAttributeValue("data-cy", "release-date").text();
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
            game.setImgURL(gamePage.getElementsByClass("jsx-2982775576 object-thumbnail").select("img").attr("src"));

                list.add(game);

        }
        Date date = new Date();
        date.setMonth(date.getMonth() + 1);
        date.setYear(date.getYear() + 1900);
        String filename = "games_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getDate() + "_" + date.getMonth() + "_" + date.getYear() + ".json";

        ParsingUtils.toSerializeJson(list, filename);
    }
}
