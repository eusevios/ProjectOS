package Parsers;
import Entities.MovieEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.print.Doc;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MovieParser extends Parser {
    private static final Logger logger = LogManager.getLogger(MovieParser.class);
    @Override
    public void parse() throws IOException, InterruptedException, ParseException {

        logger.info("Parsing of movies started");

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.ign.com/upcoming/movies");

        WebElement button1 = driver.findElement(By.className("calendar-dropdown"));
        button1.click();

        List<String> links = new ArrayList<>();

        List<WebElement> buttons = driver.findElements(By.cssSelector("button.menu-item.jsx-1036129091"));

        for (WebElement button : buttons) {
            button.click();
            Thread.sleep(2000);
            List<WebElement> currentLinks = driver.findElements(By.cssSelector("a.tile-link"));
            for (WebElement elem : currentLinks) {
                links.add(elem.getAttribute("href"));
            }
        }
        driver.quit();

        List<MovieEntity> list = new ArrayList<>();
        Document doc;
        for(String link : links){
            try {
                doc = Jsoup.connect(link).get();
            }
            catch (Exception e ){
                logger.info("Connection error: " + link, e);
                continue;
            }

                MovieEntity movieEntity = new MovieEntity();

                movieEntity.setName(doc.getElementsByClass("display-title jsx-1812565333 balanced").text());
                movieEntity.setProducers(doc.select(".object-summary-text.producers-info .small a").eachText().toArray(new String[0]));
                movieEntity.setGenres(doc.select(".object-summary-text.genres-info .small a").eachText().toArray(new String[0]));
                movieEntity.setSummary(doc.select(".object-summary-text.summary-info .interface").text());

                String date = doc.getElementsByAttributeValue("data-cy", "release-date").text();

                if (date.equals("TBA 2024")) movieEntity.setDate("TBA 2024");
                else movieEntity.setDate(ParsingUtils.formatDate(date, "MMM dd, yyyy", "dd.MM.yyyy"));

                movieEntity.setImgURL(doc.getElementsByClass("jsx-109104613 object-thumbnail").select("img").attr("src"));
                list.add(movieEntity);



        }
        Date date = new Date();
        date.setMonth(date.getMonth()+1);
        date.setYear(date.getYear() + 1900);
        String filename = "movies_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getDate() + "_" + date.getMonth() + "_" + date.getYear() + ".json";


        ParsingUtils.toSerializeJson(list, filename);


    }

}
