package airhockey.environment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GoalTest {

    @Test
    @DisplayName("Goal outside of rink should raise error")
    public void checkInitialization() {
        Rink rink = new Rink(Rink.MIN_WIDTH, Rink.MIN_HEIGHT);
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            Goal goal = new Goal(Side.LEFT, Rink.MIN_HEIGHT + 0.001f, rink.getHeight()/2f, rink);
        }, "A goal too large for the rink is not allowed.");

        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            Goal goal = new Goal(Side.LEFT, 20, 2, rink);
        }, "A goal partially/completely outside of the rink is not allowed.");
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            Goal goal = new Goal(Side.LEFT, 20, rink.getHeight(), rink);
        }, "A goal partially/completely outside of the rink is not allowed.");

        Assertions.assertDoesNotThrow(()->{
            Goal goal = new Goal(Side.LEFT, 20, rink.getHeight()/2f, rink);
        }, "A goal within bounds of the rink is allowed.");
        Assertions.assertDoesNotThrow(()->{
            Goal goal = new Goal(Side.LEFT, 50, rink.getHeight()/2f, rink);
        }, "A goal within bounds of the rink is allowed.");

    }

    @Test
    @DisplayName("A \"goal\" should only happen when Puck is touching Goal")
    public void checkGoalTrigger() {
        Rink rink = new Rink(Rink.MIN_WIDTH, Rink.MIN_HEIGHT);
        float goalSize = rink.goalLeft.getSize();
        float goalWidth = rink.goalLeft.getWidth();

        Puck puck;

        puck = new Puck(goalWidth/2 + 0.001f, rink.getHeight()/2f, 0, 0, 10, "", rink);
        Assertions.assertFalse(rink.goalLeft.isGoal(puck), "Puck should be enough right to not be in left goal");

        puck = new Puck(rink.getWidth() - goalWidth/2f - 0.001f, rink.getHeight()/2f, 0, 0, 10, "", rink);
        Assertions.assertFalse(rink.goalRight.isGoal(puck), "Puck should be enough left to not be in right goal");

        puck = new Puck(goalWidth/2, rink.getHeight()/2f + goalSize/2f + 0.001f, 0, 0, 10, "", rink);
        Assertions.assertFalse(rink.goalLeft.isGoal(puck), "Puck should be high enough to not be in goal");

        puck = new Puck(goalWidth/2, rink.getHeight()/2f - goalSize/2f - 0.001f, 0, 0, 10, "", rink);
        Assertions.assertFalse(rink.goalLeft.isGoal(puck), "Puck should be low enough to not be in goal");


        puck = new Puck(10, rink.getHeight()/2f, 0, 0, 10f, "", rink);
        Assertions.assertTrue(rink.goalLeft.isGoal(puck), "Puck should be enough left to be in left goal");

        puck = new Puck(rink.getWidth() - 10, rink.getHeight()/2f, 0, 0, 10f, "", rink);
        Assertions.assertTrue(rink.goalRight.isGoal(puck), "Puck should be enough right to be in right goal");

        puck = new Puck(10, rink.getHeight()/2f + goalSize/2f, 0, 0, 10f, "", rink);
        Assertions.assertTrue(rink.goalLeft.isGoal(puck), "Puck should high enough to be in goal");

        puck = new Puck(10, rink.getHeight()/2f - goalSize/2f, 0, 0, 10f, "", rink);
        Assertions.assertTrue(rink.goalLeft.isGoal(puck), "Puck should low enough to be in goal");
    }

}
