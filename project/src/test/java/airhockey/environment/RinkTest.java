package airhockey.environment;

import airhockey.environment.Rink;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RinkTest {
    @Test
    @DisplayName("Invalid dimension initialization gives error")
    public void checkDimensions() {

        int[][] invalidDimensions = {{-20, 20}, {20, -20}, {20, 0}, {0, 20}};

        for (int[] dimension : invalidDimensions) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                Rink rink = new Rink(dimension[0], dimension[1]);
            });
        }

        Assertions.assertDoesNotThrow(() -> {
            Rink rink = new Rink(100, 100);
        });
    }

    @Test
    @DisplayName("Props placed outside of rink gives error")
    public void itemOutOfBounds() {
        Rink rink = new Rink(100, 100);

        int[][] invalidPositions = {{-20, 20}, {20, -20}, {120, 20}, {20, 120}};

        for (int[] position : invalidPositions) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                Player player = new Player(position[0], position[1], 0, 0, 20, 20, 20, 10,
                        Side.LEFT, "Player 1", rink);
            }, "Player with initial coords %s, %s should throw exception.".formatted(position[0], position[1]));

            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                Puck  puck = new Puck(position[0], position[1], 0, 0, 10, "Puck", rink);
            }, "Puck with initial coords %s, %s should throw exception.".formatted(position[0], position[1]));
        }

    }
}
