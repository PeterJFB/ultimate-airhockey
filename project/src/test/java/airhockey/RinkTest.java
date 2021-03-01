package airhockey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RinkTest {
    @Test
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
            Rink rink = new Rink(100, 50);
        });Assertions.assertDoesNotThrow(() ->{
            Rink rink = new Rink(60, 20);
        });
    }
}
