package Parsers;
import Entities.MovieEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieParser {

    private void toSerializeJson(List<MovieEntity> albums, String filename) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(albums, writer);
        } catch (IOException ignored) {

        }
    }

    public static void parse(String filename) throws InterruptedException {

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.ign.com/upcoming/movies");

        WebElement button1 = driver.findElement(By.className("calendar-dropdown"));
        button1.click();

        List<String> links = new ArrayList<>();

        List<WebElement> buttons = driver.findElements(By.cssSelector("button.menu-item.jsx-1036129091"));

        for (WebElement button : buttons) {
            button.click();
            Thread.sleep(1000);
            List<WebElement> currentLinks = driver.findElements(By.cssSelector("a.tile-link"));
            System.out.println(currentLinks.size());
            for(WebElement elem : currentLinks){
                links.add(elem.getAttribute("href"));
            }
        }
        for (String elem : links) {
            System.out.println(elem);
        }

        driver.quit();
    }

}
