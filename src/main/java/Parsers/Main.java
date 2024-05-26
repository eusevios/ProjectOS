package Parsers;
import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

        MovieParser movieParser = new MovieParser();
        AnimeParser animeParser = new AnimeParser();
        GameParser gameParser = new GameParser();
        AlbumParser albumParser = new AlbumParser();

        albumParser.parse();
        movieParser.parse();
        animeParser.parse();
        gameParser.parse();


    }
}
