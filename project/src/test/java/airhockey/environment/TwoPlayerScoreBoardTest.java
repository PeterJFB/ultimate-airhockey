package airhockey.environment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TwoPlayerScoreBoardTest {
    @Test
    @DisplayName("Accepts only non-negative points")
    public void testNonNegativePoints() {
        TwoPlayerScoreBoard scoreBoard = new TwoPlayerScoreBoard();
        Assertions.assertDoesNotThrow(()-> {
            scoreBoard.addScore(Side.RIGHT, 1);
            scoreBoard.addScore(Side.RIGHT, 0);
        }, "Adding positive or zero points should throw no exception");
        Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            scoreBoard.addScore(Side.LEFT, -1);
        }, "Adding negative points should throw exception");
    }

    @Test
    @DisplayName("Accepts multiple points")
    public void testMultiplePoints() {
        TwoPlayerScoreBoard scoreBoard = new TwoPlayerScoreBoard();
        Assertions.assertDoesNotThrow(() -> {
            scoreBoard.addScore(Side.RIGHT, 10);
        });
        Assertions.assertEquals(10, scoreBoard.getScoreOf(Side.RIGHT));
    }

    @Test
    @DisplayName("Winner exists only when one has more points than the other")
    public void testWinner() {
        TwoPlayerScoreBoard scoreBoard = new TwoPlayerScoreBoard();

        Assertions.assertNull(scoreBoard.getWinner());

        scoreBoard.addScore(Side.LEFT, 5);
        Assertions.assertEquals(Side.LEFT, scoreBoard.getWinner());

        scoreBoard.addScore(Side.RIGHT, 5);
        Assertions.assertNull(scoreBoard.getWinner(), "There should be no winner if both have 5 points");

        scoreBoard.addScore(Side.RIGHT, 1);
        Assertions.assertEquals(Side.RIGHT, scoreBoard.getWinner());
    }
}
