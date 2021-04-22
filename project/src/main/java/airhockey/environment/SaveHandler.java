package airhockey.environment;

import airhockey.lib.SaveController;

import org.json.JSONObject;


import java.io.*;
import java.util.*;


//TODO: Give better error messages
//TODO: Write tests?
//TODO: clean code/comment everything
public class SaveHandler implements SaveController<Rink> {

    @Override
    public String accepts() {
        return "*.pson";
    }

    @Override
    public String fileDescription() {
        return "Peters Sub-par Object Notation (.pson)";
    }

    @Override
    public void save(String path, Rink rink) throws IOException {
        // https://gitlab.stud.idi.ntnu.no/tdt4100/v2021/students/-/blob/master/foreksempel/src/main/java/of10/lf/SaveHandler.java
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);
        if (rink == null)
            throw new IllegalArgumentException("rink cannot be null");

        try {
            BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(path));

            createSection(outStream, "RINK");
            writeKeyValue(outStream, "width", rink.getWidth());
            writeKeyValue(outStream, "height", rink.getHeight());

            createSection(outStream, "PLAYER_LEFT");
            writeKeyValueForPlayer(outStream, rink.playerLeft);

            createSection(outStream, "PLAYER_RIGHT");
            writeKeyValueForPlayer(outStream, rink.playerRight);

            createSection(outStream, "SCOREBOARD");
            writeKeyValue(outStream, Side.LEFT.name(), rink.scoreBoard.getScoreOf(Side.LEFT));
            writeKeyValue(outStream, Side.RIGHT.name(), rink.scoreBoard.getScoreOf(Side.RIGHT));
            writeKeyValue(outStream, "time", rink.countDown.getTime());

            for (int i = 0; i < rink.pucks.size(); i++) {
                createSection(outStream, "PUCK" + i);
                writeKeyValueForDiskObject(outStream, rink.pucks.get(i));
                writeKeyValue(outStream, "id", rink.pucks.get(i).getId());
            }

            outStream.flush();
            outStream.close();

        } catch (FileNotFoundException e) {
            System.err.println("Error: file 'test.txt' could not be opened for writing.");
            System.exit(1);
        }

    }

    @Override
    public Rink load(String path) throws IOException {
        Rink rink;

        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);


        // Convert to one string
        StringBuilder inString = new StringBuilder();

        try {
            // Read input
            BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(path));
            byte[] byteList = inStream.readAllBytes();
            inStream.close();

            for (byte b : byteList) {
                char c = (char) b;
                inString.append(c);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: file 'test.txt' could not be opened. Does it exist?");
            System.exit(1);
        }

        // Throw error if file had no data
        if (inString.length() == 0) {
            throw new IllegalArgumentException("File had no data");
        }

        // Create two-dimensional HashMap of data (inspired by jsons ObjectMapper - therefore the name - pson)
        HashMap<String, HashMap<String, String>> sections = new HashMap<>();

        // First dimension is split by two consecutive newlines
        // https://stackoverflow.com/questions/454908/split-java-string-by-new-line
        for (String sectionString : inString.toString().split("(\\R){2,}")) {
            // Second dimension is key-value pairs, split by a single newline
            String[] keyAndVals = sectionString.split("\\R");


            // Make sure the section actually has data.
            if (keyAndVals.length < 2) {
                continue;
            }

            // First index is not a key-value pair, but the name of the section
            sections.put(keyAndVals[0], new HashMap<>());
            HashMap<String, String> currentSection = sections.get(keyAndVals[0]);

            // Append all key-value pairs (separated by a ":") to the section
            // We use an iterator instead of regex since regex is not working as expected using ":"
            for (int i = 1; i < keyAndVals.length; i++) {
                String keyAndVal = keyAndVals[i];
                int splitIndex = keyAndVals[i].indexOf(':');
                if (Arrays.asList(keyAndVal.length() - 1, -1, 0).contains(splitIndex))
                    throw new InvalidPropertiesFormatException("Invalid key-value pair: " + keyAndVal);
                currentSection.put(keyAndVal.substring(0, splitIndex), keyAndVal.substring(splitIndex + 1));
            }
        }

        System.out.println(sections);
        // We now have a two-dimensional HashMap which we can extract values from.
        int width = readIntValue(sections.get("RINK"), "width");
        int height = readIntValue(sections.get("RINK"), "height");

        rink = new Rink(width, height);

        Player playerLeft = readKeyValueForPlayer(sections.get("PLAYER_LEFT"), rink);
        Player playerRight = readKeyValueForPlayer(sections.get("PLAYER_RIGHT"), rink);

        rink.setPlayer(Side.LEFT, playerLeft);
        rink.setPlayer(Side.RIGHT, playerRight);


        rink.scoreBoard.addScore(Side.LEFT, readIntValue(sections.get("SCOREBOARD"), Side.LEFT.name()));
        rink.scoreBoard.addScore(Side.RIGHT, readIntValue(sections.get("SCOREBOARD"), Side.RIGHT.name()));
        rink.countDown.setTime(readFloatValue(sections.get("SCOREBOARD"), "time"));

        rink.clearPucks();
        int i = 0;
        while (sections.containsKey("PUCK" + i)) {
            rink.pucks.add(readKeyValueForPuck(sections.get("PUCK"+i), rink));
            i++;
        }

        return rink;
    }

    private void createSection(OutputStream os, String sectionName) throws IOException {
        os.write(("\n\n" + sectionName + '\n').getBytes());
    }

    private void writeKeyValue(OutputStream os, String key, int value) throws IOException {
        os.write((key + ':' + String.valueOf(value) + '\n').getBytes());
    }

    private void writeKeyValue(OutputStream os, String key, float value) throws IOException {
        os.write((key + ':' + String.valueOf(value) + '\n').getBytes());
    }

    private void writeKeyValue(OutputStream os, String key, String value) throws IOException {
        os.write((key + ':' + String.valueOf(value) + '\n').getBytes());
    }

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

    private int readIntValue(HashMap<String, String> section, String key) {
        return Integer.parseInt(section.get(key));
    }

    private float readFloatValue(HashMap<String, String> section, String key) {
        return Float.parseFloat(section.get(key));
    }

    private String readString(HashMap<String, String> section, String key) {
        return String.valueOf(section.get(key));
    }

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
        Side side = Side.valueOf(readString(section, "side"));

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
}
