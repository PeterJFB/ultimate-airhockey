package airhockey.environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import airhockey.lib.SaveController;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


import java.io.*;

public class SaveHandler implements SaveController<Rink> {

    @Override
    public void save(String path, Rink object) throws IOException {
        // https://gitlab.stud.idi.ntnu.no/tdt4100/v2021/students/-/blob/master/foreksempel/src/main/java/of10/lf/SaveHandler.java
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);
        if (object == null)
            throw new IllegalArgumentException("Object cannot be null");

        FileOutputStream fos = new FileOutputStream(path.endsWith(".json") ? path : path + ".json");
        (new ObjectMapper()).writeValue(fos, object);
        fos.close();

    }

    @Override
    public Rink load(String path) throws IOException {
        Rink rink;

        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);

        FileReader fr = new FileReader(path);

        // https://www.baeldung.com/java-org-json
        JSONTokener tokener = new JSONTokener(fr);
        JSONObject jsonObject = new JSONObject(tokener);

        int width = jsonObject.getInt("width");
        int height = jsonObject.getInt("height");

        rink = new Rink(width, height);

        // Player
        Player playerLeft = getPlayerFromJSONObject((JSONObject) jsonObject.get("playerLeft"), rink);
        Player playerRight = getPlayerFromJSONObject((JSONObject) jsonObject.get("playerRight"), rink);

        rink.setPlayer(Side.LEFT, playerLeft);
        rink.setPlayer(Side.RIGHT, playerRight);

        // Pucks
        rink.clearPucks();
        JSONArray pucksArray = jsonObject.getJSONArray("pucks");
        for (Object puckObject : pucksArray) {
            rink.pucks.add(getPuckFromJSONObject((JSONObject) puckObject, rink));
        }

        JSONObject scoreBoardObject = jsonObject.getJSONObject("scoreBoard").getJSONObject("scores");
        rink.scoreBoard.addScore(Side.LEFT, scoreBoardObject.getInt(Side.LEFT.name()));
        rink.scoreBoard.addScore(Side.RIGHT, scoreBoardObject.getInt(Side.RIGHT.name()));

        JSONObject countDownObject = jsonObject.getJSONObject("countDown");
        rink.countDown.setTime(countDownObject.getFloat("time"));

        return rink;
    }

    private JSONObject getJSONObjectFromDiskObject(diskObject diskObject) {
        JSONObject object = new JSONObject();
        object.put("x", diskObject.getX());
        object.put("y", diskObject.getY());
        object.put("vx", diskObject.getVx());
        object.put("vy", diskObject.getVy());
        object.put("radius", diskObject.getRadius());
        return object;
    }

    private JSONObject getJSONObjectFromPuck(Puck puck) {
        JSONObject puckObject = getJSONObjectFromDiskObject(puck);
        puckObject.put("Id", puck.getId());

        return puckObject;
    }

    private Player getPlayerFromJSONObject(JSONObject playerObject, Rink rink) {
        float x = playerObject.getFloat("x");
        float y = playerObject.getFloat("y");
        float vx = playerObject.getFloat("vx");
        float vy = playerObject.getFloat("vy");
        float radius = playerObject.getFloat("radius");
        float originX = playerObject.getFloat("originX");
        float reachX = playerObject.getFloat("reachX");
        float reachY = playerObject.getFloat("reachY");
        String name = playerObject.getString("name");
        Side side = Side.valueOf(playerObject.getString("side"));

        return new Player(x, y, vx, vy, originX, reachX, reachY, radius, side, name, rink);
    }

    private Puck getPuckFromJSONObject(JSONObject puckObject, Rink rink) {
        float x = puckObject.getFloat("x");
        float y = puckObject.getFloat("y");
        float vx = puckObject.getFloat("vx");
        float vy = puckObject.getFloat("vy");
        float radius = puckObject.getFloat("radius");
        String id = puckObject.getString("id");
        return new Puck(x, y, vx, vy, radius, id, rink);
    }
}
