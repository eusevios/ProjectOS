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
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class GameParser extends Parser {
    private static final Logger logger = LogManager.getLogger(GameParser.class);

    @Override
    void parse() throws IOException, InterruptedException, ParseException {

        logger.info("Parsing of games started");

        //Set the path to the ChromeDriver binary
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");

        // Set Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-debugging-port=9222");
        options.addArguments("--disable-gpu");

        // Initialize ChromeDriver with options
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.ign.com/upcoming/games");

        WebElement CalendarButton = driver.findElement(By.className("calendar-dropdown"));
        CalendarButton.click();

        List<String> URLs = new ArrayList<>();

        List<WebElement> buttons = driver.findElements(By.cssSelector("button.menu-item.jsx-1036129091"));

        int i = -1;
        Date currentDate = new Date();
        int currentMonth = currentDate.getMonth();
        for (WebElement button : buttons) {
            ++i;
            if (i < currentMonth) continue;
            button.click();
            Thread.sleep(2000);
            List<WebElement> currentLinks = driver.findElements(By.cssSelector("a.tile-link"));
            for (WebElement elem : currentLinks) {
                if (elem.findElement(By.cssSelector("div.interface.jsx-2563906438.jsx-2321054750.tile-meta.small")).getText().contains("/"))
                    break;
                URLs.add(elem.getAttribute("href"));
            }
        }
        driver.quit();
        List<GameEntity> gameEntities = new ArrayList<>();
        Document gamePage;
        for (String url : URLs) {
            try {
                gamePage = Jsoup.connect(url).get();
            } catch (Exception e) {
                logger.info("Connection error: " + url, e);
                continue;
            }
            GameEntity game = new GameEntity();
            game.setName(gamePage.getElementsByClass("display-title jsx-1812565333 balanced").text());
            game.setPublishers(gamePage.select(".object-summary-text.publishers-info .small a").text());
            game.setDevelopers(gamePage.select(".object-summary-text.developers-info .small a").text());
            game.setFranchises(gamePage.select(".object-summary-text.franchises-info .small a").text());
            game.setGenres(gamePage.select(".object-summary-text.genres-info .small a").eachText().toArray(new String[0]));
            game.setPlatforms(gamePage.select(".object-summary-text.platforms-info .platforms a").eachAttr("title").toArray(new String[0]));
            game.setSummary(gamePage.select(".object-summary-text.summary-info .interface").text());
            String date = gamePage.getElementsByAttributeValue("data-cy", "release-date").text();
            if (date.contains(",")) game.setDate(ParsingUtils.formatDate(date, "MMM dd, yyyy", "dd.MM.yyyy"));
            else {
                game.setDate(ParsingUtils.formatDate(date, "MMMM yyyy", "MM.yyyy"));
            }
            game.setImgURL(gamePage.select("img.jsx-2920405963.progressive-image.object-image.jsx-405688819.expand").attr("src"));
            gameEntities.add(game);

        }
        Date date = new Date();
        date.setMonth(date.getMonth() + 1);
        date.setYear(date.getYear() + 1900);
        String filename = "games_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getDate() + "_" + date.getMonth() + "_" + date.getYear() + ".json";

        ParsingUtils.toSerializeJson(gameEntities, filename);
    }
}
