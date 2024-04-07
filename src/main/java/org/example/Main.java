package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ParserAlbums parser = new ParserAlbums();
        parser.parse("js1.json");
    }
}
