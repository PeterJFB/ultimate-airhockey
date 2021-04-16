package airhockey.environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import airhockey.lib.SaveController;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class SaveHandler implements SaveController<Rink> {

    JSONParser parser = new JSONParser();

    @Override
    public void save(String path, Rink object) throws IOException {
        // https://gitlab.stud.idi.ntnu.no/tdt4100/v2021/students/-/blob/master/foreksempel/src/main/java/of10/lf/SaveHandler.java
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);
        if (object == null || path.isBlank())
            throw new IllegalArgumentException("Object cannot be null");

        FileOutputStream fos = new FileOutputStream(path.endsWith(".json") ? path : path + ".json");
        (new ObjectMapper()).writeValue(fos, object);
        fos.close();

    }

    @Override
    public Rink load(String path) throws IOException, ParseException {
        Rink rink;

        // https://crunchify.com/how-to-read-json-object-from-file-in-java/
        Object obj = parser.parse(new FileReader(path));

        JSONObject jsonObject = (JSONObject) obj;

        int width = ((Long) jsonObject.get("width")).intValue();
        int height = ((Long) jsonObject.get("height")).intValue();

        rink = new Rink(width, height);

        // Player
        Player playerLeft = getPlayerFromJSONObject((JSONObject) jsonObject.get("playerLeft"), rink);
        Player playerRight = getPlayerFromJSONObject((JSONObject) jsonObject.get("playerRight"), rink);

        rink.setPlayer(Side.LEFT, playerLeft);
        rink.setPlayer(Side.RIGHT, playerRight);

        // Pucks
        rink.clearPucks();
        JSONArray pucksArray = (JSONArray) jsonObject.get("pucks");
        for (Object puckObject : pucksArray) {
            rink.pucks.add(getPuckFromJSONObject((JSONObject) puckObject, rink));
        }

        JSONObject scoreBoardObject = (JSONObject) ((JSONObject) jsonObject.get("scoreBoard")).get("scores");
        rink.scoreBoard.addScore(Side.LEFT, ((Long) scoreBoardObject.get(Side.LEFT.name())).intValue());
        rink.scoreBoard.addScore(Side.RIGHT, ((Long) scoreBoardObject.get(Side.RIGHT.name())).intValue());

        JSONObject countDownObject = (JSONObject) jsonObject.get("countDown");
        rink.countDown.setTime(((Double) countDownObject.get("time")).floatValue());

        return rink;
    }

    private Player getPlayerFromJSONObject(JSONObject playerObject, Rink rink) {
        float x = ((Double) playerObject.get("x")).floatValue();
        float y = ((Double) playerObject.get("y")).floatValue();
        float vx = ((Double) playerObject.get("vx")).floatValue();
        float vy = ((Double) playerObject.get("vy")).floatValue();
        float radius = ((Double) playerObject.get("radius")).floatValue();
        float originX = ((Double) playerObject.get("originX")).floatValue();
        float reachX = ((Double) playerObject.get("reachX")).floatValue();
        float reachY = ((Double) playerObject.get("reachY")).floatValue();
        String name = (String) playerObject.get("name");
        Side side = Side.valueOf((String) playerObject.get("side"));

        return new Player(x, y, vx, vy, originX, reachX, reachY, radius, side, name, rink);
    }

    private Puck getPuckFromJSONObject(JSONObject puckObject, Rink rink) {
        float x = ((Double) puckObject.get("x")).floatValue();
        float y = ((Double) puckObject.get("y")).floatValue();
        float vx = ((Double) puckObject.get("vx")).floatValue();
        float vy = ((Double) puckObject.get("vy")).floatValue();
        float radius = ((Double) puckObject.get("radius")).floatValue();
        String id = (String) puckObject.get("id");
        return new Puck(x, y, vx, vy, radius, id, rink);
    }
}
