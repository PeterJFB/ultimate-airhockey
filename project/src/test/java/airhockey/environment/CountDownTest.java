package airhockey.environment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CountDownTest {
    @Test
    @DisplayName("Invalid initialization should give error")
    public void checkInitialization() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CountDown cd = new CountDown(-1, 1);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CountDown cd = new CountDown(100, 0);
        });
    }

    @Test
    @DisplayName("Countdown should tick as set at initialization")
    public void testCountDownCounting() {
        CountDown cd = new CountDown(100, 0.7f);
        Assertions.assertEquals(100f, cd.getTime());
        cd.tick();
        Assertions.assertEquals(99.3f, cd.getTime());
        Assertions.assertEquals(100, cd.getTimeInWholeSeconds());
    }

    @Test
    @DisplayName("Countdown should stop counting when it is finished")
    public void testFinishedCountdown() {
        CountDown cd = new CountDown(1, 0.5f);

        Assertions.assertFalse(cd.isFinished());
        cd.tick();
        cd.tick();
        Assertions.assertTrue(cd.isFinished());

        float stoppedTime = cd.getTime();
        cd.tick();
        Assertions.assertEquals(stoppedTime, cd.getTime(), "The timer should not have change since it is already finished");

    }

    @Test
    @DisplayName("Countdown cannot be set to an invalid time")
    public void testSetCountDown() {
        CountDown cd = new CountDown(10, 0.5f);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           cd.setTime(-0.001f);
        }, "A negative time should throw error");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cd.setTime(10.001f);
        }, "A time larger than initial time should throw error");
    }
}
