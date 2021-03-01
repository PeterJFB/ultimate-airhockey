package airhockey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PuckTest {
    @Test
    public void checkInitialization() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Rink rink = new Rink(60, 20);
            Puck puck = new Puck(10, -10, 0, 0, rink, 20);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Rink rink = new Rink(60, 20);
            Puck puck = new Puck(10, 6, 0, 0, rink, 0);
        });

        Assertions.assertDoesNotThrow(() -> {
            Rink rink = new Rink(60, 20);
            Puck puck = new Puck(10, 6, 0, 0, rink, 1);
        });
    }
}
