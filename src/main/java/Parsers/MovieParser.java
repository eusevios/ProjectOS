package Parsers;

import Entities.MovieEntity;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieParser extends Parser {
    private static final Logger logger = LogManager.getLogger(MovieParser.class);

    @Override
    public void parse() throws IOException, InterruptedException, ParseException {

        logger.info("Parsing of movies started");

        // Set the path to the ChromeDriver binary
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
        driver.get("https://www.ign.com/upcoming/movies");

        // Assuming driver is your WebDriver instance
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement calendarButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button.calendar-dropdown")));
        calendarButton.click();

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
        List<MovieEntity> movieEntities = new ArrayList<>();
        Document moviePage;
        for (String url : URLs) {
            try {
                moviePage = Jsoup.connect(url).get();
            } catch (Exception e) {
                logger.info("Connection error: " + url, e);
                continue;
            }
            MovieEntity movieEntity = new MovieEntity();
            movieEntity.setName(moviePage.getElementsByClass("display-title jsx-1812565333 balanced").text());
            movieEntity.setProducers(moviePage.select(".object-summary-text.producers-info .small a").eachText().toArray(new String[0]));
            movieEntity.setGenres(moviePage.select(".object-summary-text.genres-info .small a").eachText().toArray(new String[0]));
            movieEntity.setSummary(moviePage.select(".object-summary-text.summary-info .interface").text());
            String date = moviePage.getElementsByAttributeValue("data-cy", "release-date").text();
            movieEntity.setDate(ParsingUtils.formatDate(date, "MMM dd, yyyy", "dd.MM.yyyy"));
            movieEntity.setImgURL(moviePage.select("img.jsx-2920405963.progressive-image.object-image.jsx-405688819.expand").attr("src"));
            movieEntities.add(movieEntity);
        }
        Date date = new Date();
        date.setMonth(date.getMonth() + 1);
        date.setYear(date.getYear() + 1900);
        String filename = "movies_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getDate() + "_" + date.getMonth() + "_" + date.getYear() + ".json";
        ParsingUtils.toSerializeJson(movieEntities, filename);
    }
}
