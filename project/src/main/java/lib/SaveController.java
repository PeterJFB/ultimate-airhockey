package lib;

import org.json.simple.parser.ParseException;

import java.io.IOException;

public interface SaveController<T> {

    void save(String path, T object) throws IOException;

    T load(String filename) throws IOException, ParseException;

}
