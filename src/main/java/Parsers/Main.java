package Parsers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ParserAlbums parser = new ParserAlbums();
        parser.parse("js1.json");
    }
}
