package Parsers;
import Entities.MovieEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
import java.util.List;

public class MovieParser extends Parser {
    @Override
    public void parse(String filename) throws IOException, InterruptedException, ParseException {

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

        int k = 0;
        List<MovieEntity> list = new ArrayList<>();
        for(String link : links){
            Document doc = Jsoup.connect(link).get();
            MovieEntity movieEntity = new MovieEntity();
            movieEntity.name = doc.getElementsByClass("display-title jsx-1812565333 balanced").text();
            movieEntity.producers = doc.select(".object-summary-text.producers-info .small a").eachText().toArray(new String[0]);
            movieEntity.genres = doc.select(".object-summary-text.genres-info .small a").eachText().toArray(new String[0]);
            movieEntity.summary =  doc.select(".object-summary-text.summary-info .interface").text();

            String date = doc.getElementsByAttributeValue("data-cy", "release-date").text();
            if(date.equals("TBA 2024")) movieEntity.date = "TBA 2024";
            else movieEntity.date = ParsingUtils.formatDate(date, "MMM dd, yyyy", "dd.MM.yyyy");


            movieEntity.imgURL = doc.getElementsByClass("jsx-109104613 object-thumbnail").select("img").attr("src");
            list.add(movieEntity);

        }
        ParsingUtils.toSerializeJson(list, filename);


    }

}
