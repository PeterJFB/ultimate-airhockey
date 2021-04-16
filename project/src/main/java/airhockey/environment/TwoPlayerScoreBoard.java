package airhockey.environment;

import java.util.HashMap;

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

    public void setPlayer1Name(String player1Name) {
        if (player1Name.equals(player2Name))
            throw new IllegalArgumentException("player1Name must differ from player2Name" + player1Name);

        int player1Score = scores.get(this.player1Name);
        scores.put(player1Name, player1Score);
        this.player1Name = player1Name;

    }

    public void setPlayer2Name(String player2Name) {
        if (player2Name.equals(player1Name))
            throw new IllegalArgumentException("player2Name must differ from player1Name" + player2Name);

        int player2Score = scores.get(this.player2Name);
        scores.put(player2Name, player2Score);
        this.player2Name = player2Name;
    }

    public void addScore(String playerName, int points) {
        if (!playerName.equals(player1Name) && !playerName.equals(player2Name))
            throw new IllegalArgumentException("playerName is not matching any of the players: " + playerName);
        if (points < 0)
            throw new IllegalArgumentException("points cannot be negative: " + points);

        int oldScore = scores.get(playerName);
        scores.put(playerName, oldScore + points);
    }

    public HashMap<String, Integer> getScore() {
        return scores;
    }

    public String getWinner() {
        int player1Score = scores.get(player1Name);
        int player2Score = scores.get(player2Name);
        if (player1Score > player2Score)
            return player1Name;
        else if (player1Score < player2Score)
            return player2Name;
        else
            return "";
    }

    // TODO: Test scores are updated correctly
}
