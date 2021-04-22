package airhockey.environment;

import airhockey.lib.SaveController;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

//use of json serializer https://piazza.com/class/kjjy01odz6mmq?cid=954
/*
 * I know we are encouraged to not use existing libraries where you do not need to manually serialize/deserialize verbatim.
 * But given the size of the project, I decided it was most realistic to represent the save file in a standardized format, and
 * since jackson was approved on piazza i choose JSON. I am still required to write my own serialization/deserialization functions, as i have done here.
 * */

public class SaveHandlerButBetter implements SaveController<Rink> {

    @Override
    public void save(String path, Rink rink) throws IOException {
        // https://gitlab.stud.idi.ntnu.no/tdt4100/v2021/students/-/blob/master/foreksempel/src/main/java/of10/lf/SaveHandler.java
        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);
        if (rink == null)
            throw new IllegalArgumentException("rink cannot be null");

        JSONObject rinkObject = new JSONObject();
        rinkObject.put("width", rink.getWidth());
        rinkObject.put("height", rink.getHeight());

        rinkObject.put("playerLeft", getJSONObjectFromPlayer(rink.playerLeft));
        rinkObject.put("playerRight", getJSONObjectFromPlayer(rink.playerRight));

        JSONArray puckArray = new JSONArray();
        for (Puck puck : rink.pucks) {
            puckArray.put(getJSONObjectFromPuck(puck));
        }
        rinkObject.put("pucks", puckArray);

        rinkObject.put("scoreBoard", getJSONObjectFromScoreBoard(rink.scoreBoard));
        rinkObject.put("time", rink.countDown.getTime());

        System.out.println(rinkObject);

        PrintWriter outFile = new PrintWriter(path);
        outFile.println(rinkObject);
        outFile.close();

    }

    @Override
    public Rink load(String path) throws IOException {
        Rink rink;

        if (path == null || path.isBlank())
            throw new IllegalArgumentException("Path cannot be null or blank: " + path);

        // https://www.baeldung.com/java-org-json
        JSONTokener tokener = new JSONTokener(new FileReader(path));
        JSONObject jsonObject = new JSONObject(tokener);

        int width = jsonObject.getInt("width");
        int height = jsonObject.getInt("height");

        rink = new Rink(width, height);

        // Player
        Player playerLeft = getPlayerFromJSONObject(jsonObject.getJSONObject("playerLeft"), rink);
        Player playerRight = getPlayerFromJSONObject(jsonObject.getJSONObject("playerRight"), rink);

        rink.setPlayer(Side.LEFT, playerLeft);
        rink.setPlayer(Side.RIGHT, playerRight);

        // Pucks
        rink.clearPucks();
        JSONArray pucksArray = jsonObject.getJSONArray("pucks");
        for (Object puckObject : pucksArray) {
            rink.pucks.add(getPuckFromJSONObject((JSONObject) puckObject, rink));
        }

        JSONObject scoreBoardObject = jsonObject.getJSONObject("scoreBoard");
        rink.scoreBoard.addScore(Side.LEFT, scoreBoardObject.getInt(Side.LEFT.name()));
        rink.scoreBoard.addScore(Side.RIGHT, scoreBoardObject.getInt(Side.RIGHT.name()));

        rink.countDown.setTime(jsonObject.getFloat("time"));

        return rink;
    }

    private JSONObject getJSONObjectFromDiskObject(DiskObject diskObject) {
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
        puckObject.put("id", puck.getId());

        return puckObject;
    }
    
    private JSONObject getJSONObjectFromPlayer(Player player) {
        JSONObject playerObject = getJSONObjectFromDiskObject(player);
        playerObject.put("id", player.getId());
        playerObject.put("originX", player.getOriginX());
        playerObject.put("reachX", player.getReachX());
        playerObject.put("reachY", player.getReachY());
        playerObject.put("name", player.getName());
        playerObject.put("side", player.getSide());
        return playerObject;
    }

    private JSONObject getJSONObjectFromScoreBoard(TwoPlayerScoreBoard scoreBoard) {
        JSONObject object = new JSONObject();
        object.put(Side.LEFT.name(), scoreBoard.getScoreOf(Side.LEFT));
        object.put(Side.RIGHT.name(), scoreBoard.getScoreOf(Side.RIGHT));
        return object;
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
