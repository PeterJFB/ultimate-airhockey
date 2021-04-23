package airhockey.environment;

import airhockey.lib.SaveController;

import org.json.JSONObject;


import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;


//TODO: Write tests?
public class SaveHandler implements SaveController<Rink> {

    // Description

    @Override
    public String accepts() {
        return "*.pson";
    }

    @Override
    public String fileDescription() {
        return "Peter's Sub-par Object Notation (.pson)";
    }

    // SAVING

    @Override
    public void save(String path, Rink rink) throws IOException {
        // https://gitlab.stud.idi.ntnu.no/tdt4100/v2021/students/-/blob/master/foreksempel/src/main/java/of10/lf/SaveHandler.java

        // Test valid input
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);
        if (rink == null)
            throw new IllegalArgumentException("rink cannot be null");

        // Attempt to write rink/environment of game
        writeToOutputStream(new FileOutputStream(path), rink);
    }

    void writeToOutputStream(OutputStream outputStream, Rink rink) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(outputStream)) {

            // RINK/ENVIRONMENT
            createSection(bos, "RINK");
            writeKeyValue(bos, "width", rink.getWidth());
            writeKeyValue(bos, "height", rink.getHeight());

            // PLAYERS
            createSection(bos, "PLAYER_LEFT");
            writeKeyValueForPlayer(bos, rink.playerLeft);
            createSection(bos, "PLAYER_RIGHT");
            writeKeyValueForPlayer(bos, rink.playerRight);

            // SCOREBOARD
            createSection(bos, "SCOREBOARD");
            writeKeyValue(bos, Side.LEFT.name(), rink.scoreBoard.getScoreOf(Side.LEFT));
            writeKeyValue(bos, Side.RIGHT.name(), rink.scoreBoard.getScoreOf(Side.RIGHT));
            writeKeyValue(bos, "time", rink.countDown.getTime());

            // PUCK
            for (int i = 0; i < rink.pucks.size(); i++) {
                createSection(bos, "PUCK" + i);
                writeKeyValueForDiskObject(bos, rink.pucks.get(i));
                writeKeyValue(bos, "id", rink.pucks.get(i).getId());
            }

            bos.flush();

            // Error is handled in GameController.
        }
    }

    // LOADING

    @Override
    public Rink load(String path) throws IOException {
        Rink rink;

        // Test valid input
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);

        // Attempt to create rink/environment for game
        rink = readFromInputStream(new FileInputStream(path));

        return rink;
    }

    Rink readFromInputStream(InputStream inputStream) throws IOException {
        Rink rink;

        // Create a string from input
        StringBuilder inString = new StringBuilder();
        try (BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            byte[] byteList = bis.readAllBytes();

            for (byte b : byteList) {
                char c = (char) b;
                inString.append(c);
            }

            // Error is handled in GameController.
        }

        // Throw error if file had no data
        if (inString.length() == 0) {
            throw new IOException("File has no data");
        }

        // Create two-dimensional HashMap of strings (inspired by jsons ObjectMapper - therefore the name - pson)
        // This makes the file and value extraction more readable, as every value is paired with a key.
        HashMap<String, HashMap<String, String>> sections = new HashMap<>();

        // First dimension, called sections, is split by at least two consecutive newlines
        // https://stackoverflow.com/questions/454908/split-java-string-by-new-line
        for (String sectionString : inString.toString().split("(\\R){2,}")) {
            // Second dimension is key-value pairs, split by a single newline
            String[] keyAndVals = sectionString.split("\\R");

            // Make sure the section actually has data.
            if (keyAndVals.length < 2) {
                continue;
            }

            // First index is not a key-value pair, but the name of the section
            HashMap<String, String> newSection = new HashMap<>();
            sections.put(keyAndVals[0], newSection);

            // Append all key-value pairs (separated by a ":") to the new section
            // We use an iterator instead of regex since regex is unfortunately not working as expected using colon (":" or "\\:")
            for (int i = 1; i < keyAndVals.length; i++) {
                String keyAndVal = keyAndVals[i];
                int splitIndex = keyAndVals[i].indexOf(':');

                // Make sure the split index exists, as well as not being the first or last index.
                if (Arrays.asList(-1, 0, keyAndVal.length() - 1).contains(splitIndex))
                    throw new InvalidPropertiesFormatException("Invalid key-value pair: " + keyAndVal);

                // Append key-value pair.
                newSection.put(keyAndVal.substring(0, splitIndex), keyAndVal.substring(splitIndex + 1));
            }
        }

        // System.out.println(sections);

        // We now have a two-dimensional HashMap which we can use to extract values.

        // RINK/ENVIRONMENT
        int width = readIntValue(sections.get("RINK"), "width");
        int height = readIntValue(sections.get("RINK"), "height");

        rink = new Rink(width, height);

        // PLAYERS
        Player playerLeft = readKeyValueForPlayer(sections.get("PLAYER_LEFT"), rink);
        Player playerRight = readKeyValueForPlayer(sections.get("PLAYER_RIGHT"), rink);

        rink.setPlayer(Side.LEFT, playerLeft);
        rink.setPlayer(Side.RIGHT, playerRight);

        // SCOREBOARD
        rink.scoreBoard.addScore(Side.LEFT, readIntValue(sections.get("SCOREBOARD"), Side.LEFT.name()));
        rink.scoreBoard.addScore(Side.RIGHT, readIntValue(sections.get("SCOREBOARD"), Side.RIGHT.name()));
        rink.countDown.setTime(readFloatValue(sections.get("SCOREBOARD"), "time"));

        // PUCKS
        rink.clearPucks();
        int i = 0;
        while (sections.containsKey("PUCK" + i)) {
            rink.pucks.add(readKeyValueForPuck(sections.get("PUCK" + i), rink));
            i++;
        }

        return rink;
    }

    // HELPER FUNCTIONS

    // Writing

    private void writeKeyValueForDiskObject(OutputStream os, DiskObject disk) throws IOException {
        writeKeyValue(os, "x", disk.getX());
        writeKeyValue(os, "y", disk.getY());
        writeKeyValue(os, "vx", disk.getVx());
        writeKeyValue(os, "vy", disk.getVy());
        writeKeyValue(os, "radius", disk.getRadius());
    }

    private void writeKeyValueForPlayer(OutputStream os, Player player) throws IOException {
        writeKeyValueForDiskObject(os, player);
        writeKeyValue(os, "id", player.getId());
        writeKeyValue(os, "originX", player.getOriginX());
        writeKeyValue(os, "reachX", player.getReachX());
        writeKeyValue(os, "reachY", player.getReachY());
        writeKeyValue(os, "name", player.getName());
        writeKeyValue(os, "side", player.getSide().name());
    }

    private void createSection(OutputStream os, String sectionName) throws IOException {
        os.write(("\n\n" + sectionName + '\n').getBytes());
    }

    private void writeKeyValue(OutputStream os, String key, int value) throws IOException {
        os.write((key + ':' + value + '\n').getBytes());
    }

    private void writeKeyValue(OutputStream os, String key, float value) throws IOException {
        os.write((key + ':' + value + '\n').getBytes());
    }

    private void writeKeyValue(OutputStream os, String key, String value) throws IOException {
        os.write((key + ':' + value + '\n').getBytes());
    }

    // Reading

    private Player readKeyValueForPlayer(HashMap<String, String> section, Rink rink) {
        float x = readFloatValue(section, "x");
        float y = readFloatValue(section, "y");
        float vx = readFloatValue(section, "vx");
        float vy = readFloatValue(section, "vy");
        float radius = readFloatValue(section, "radius");
        float originX = readFloatValue(section, "originX");
        float reachX = readFloatValue(section, "reachX");
        float reachY = readFloatValue(section, "reachY");
        String name = readString(section, "name");
        Side side = readSide(section, "side");

        return new Player(x, y, vx, vy, originX, reachX, reachY, radius, side, name, rink);
    }

    private Puck readKeyValueForPuck(HashMap<String, String> section, Rink rink) {
        float x = readFloatValue(section, "x");
        float y = readFloatValue(section, "y");
        float vx = readFloatValue(section, "vx");
        float vy = readFloatValue(section, "vy");
        float radius = readFloatValue(section, "radius");
        String id = readString(section, "id");

        return new Puck(x, y, vx, vy, radius, id, rink);
    }

    private int readIntValue(HashMap<String, String> section, String key) {
        validateKey(section, key);
        // Make sure the parsing is actually possible, if not throw a more fitting exception.
        try {
            return Integer.parseInt(section.get(key));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Expected value of %s to be an integer: %s".formatted(key, section.get(key)));
        }
    }

    private float readFloatValue(HashMap<String, String> section, String key) {
        validateKey(section, key);
        // Make sure the parsing is actually possible, if not throw a more fitting exception.
        try {
            return Float.parseFloat(section.get(key));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Expected value of %s to be a float: %s".formatted(key, section.get(key)));
        }
    }

    private String readString(HashMap<String, String> section, String key) {
        validateKey(section, key);
        return String.valueOf(section.get(key));
    }

    private Side readSide(HashMap<String, String> section, String key) {
        validateKey(section, key);
        // Make sure the parsing is actually possible, if not throw a more fitting exception.
        try {
            return Side.valueOf(section.get(key));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Expected value of %s to be \"LEFT\" or \"RIGHT\": %s".formatted(key, section.get(key)));
        }
    }

    private void validateKey (HashMap<String, String> section, String key) {
        // Tell players if key/parameter does not exist
        if (!section.containsKey(key)) {
            throw new InvalidParameterException("Missing parameter %s".formatted(key));
        }
    }
}
