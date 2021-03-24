package airhockey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RinkTest {
    @Test
    @DisplayName("Invalid dimension initialization gives error")
    public void checkDimensions() {

        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            Rink rink = new Rink(-20, 20);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            Rink rink = new Rink(20, 0);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            Rink rink = new Rink(20, 0);
        });

        Assertions.assertDoesNotThrow(() ->{
            Rink rink = new Rink(100, 100);
        });
        // TODO: Test deklarasjon av objekter
        // TODO: Test isCollidingWith funker som den skal.

    }
}
