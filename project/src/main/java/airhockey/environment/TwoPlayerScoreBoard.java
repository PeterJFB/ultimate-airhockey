package airhockey.environment;

import java.util.HashMap;

/*
* Delegate used by rink to keep track of scores and decide winner
* */
class TwoPlayerScoreBoard {

    private final HashMap<Side, Integer> scores;

    public TwoPlayerScoreBoard() {
        scores = new HashMap<>();
        scores.put(Side.LEFT, 0);
        scores.put(Side.RIGHT, 0);
    }

    public void addScore(Side side, int points) {
        if (side == null)
            throw new IllegalArgumentException("side cannot be null.");
        if (points < 0)
            throw new IllegalArgumentException("points cannot be negative: " + points);

        int oldScore = scores.get(side);
        scores.put(side, oldScore + points);
    }

    public int getScoreOf(Side side) {
        if (side == null)
            throw new IllegalArgumentException("side cannot be null.");
        return scores.get(side);
    }

    public Side getWinner() {
        int playerLeftScore = getScoreOf(Side.LEFT);
        int playerRightScore = getScoreOf(Side.RIGHT);

        if (playerLeftScore > playerRightScore)
            return Side.LEFT;
        else if (playerLeftScore < playerRightScore)
            return Side.RIGHT;
        else
            return null;
    }
}
