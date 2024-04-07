package Parsers;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        MovieParser parser = new MovieParser();
        parser.parse("js2.json");
    }
}
