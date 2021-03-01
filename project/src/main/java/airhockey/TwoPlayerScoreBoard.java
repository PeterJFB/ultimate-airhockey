package airhockey;

import java.util.HashMap;
import java.util.Map;

public class TwoPlayerScoreBoard {
    private String player1Name;
    private String player2Name;

    private final HashMap<String, Integer> scores;

    public TwoPlayerScoreBoard(String player1Name, String player2Name) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;

        scores = new HashMap<>();
        scores.put(player1Name, 0);
        scores.put(player2Name, 0);

    }

    public void addScore(String playerName, int points) {
        int oldScore = scores.get(playerName);
        scores.put(playerName, oldScore + points);
    }

    public HashMap<String, Integer> getScore() {
        return scores;
    }
}
