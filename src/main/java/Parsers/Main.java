package Parsers;

import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
        Parser parser = new AnimeParser();
        parser.parse("Anime.json");
    }
}
