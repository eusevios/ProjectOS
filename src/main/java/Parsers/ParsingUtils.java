package Parsers;
import Entities.Entity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ParsingUtils {

    protected static void toSerializeJson(List<? extends Entity> albums, String filename){
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(albums, writer);
        }
        catch (IOException ignored) {

        }
    }
    protected static String formatDate(String inputDate, String format) {
        SimpleDateFormat originalFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date date = originalFormat.parse(inputDate);
            String formattedDate = targetFormat.format(date);
            return formattedDate;
        }
        catch (ParseException ignored) {
            return "";
        }

    }

}
