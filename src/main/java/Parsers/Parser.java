package Parsers;
import java.io.IOException;
import java.text.ParseException;


public abstract class Parser {

    abstract void parse(String filename) throws IOException, InterruptedException, ParseException;

}
