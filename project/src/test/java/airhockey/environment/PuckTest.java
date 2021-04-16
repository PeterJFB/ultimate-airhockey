package airhockey.environment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PuckTest {
    @Test
    @DisplayName("Puck spawning out of bounds should give error")
    public void checkInitialization() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Rink rink = new Rink(60, 40);
            Puck puck = new Puck(10, -10, 0, 0, 20, rink);
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Rink rink = new Rink(60, 40);
            Puck puck = new Puck(10, 6, 0, 0, 0, rink);
        });

        Assertions.assertDoesNotThrow(() -> {
            Rink rink = new Rink(100, 100);
            Puck puck = new Puck(10, 6, 0, 0, 1, rink);
        });
    }

    @Test
    @DisplayName("Collisions with player")
    public void testCollisionWithPlayer() {
        Rink rink = new Rink(1000, 600);

        Puck puck2 = new Puck(rink.playerRight.getX() - rink.playerRight.getRadius() - 10.001f, rink.playerRight.getY(), 10f, 0f, 10f, "testPuck2", rink);
        rink.pucks.add(puck2);
        Assertions.assertFalse(puck2.isCollidingWith(rink.playerRight),
                "Place a puck close but not touching player");

        rink.tick();
        Assertions.assertTrue(puck2.isCollidingWith(rink.playerRight),
                "Move puck towards player. They should now be colliding");

        rink.tick();
        Assertions.assertTrue(puck2.getX() + puck2.getRadius() > rink.playerRight.getX() - rink.playerRight.getRadius()  && !puck2.isCollidingWith(rink.playerRight),
                "A puck right after touching a player should not collide a second time, even though they're still intersecting");

        Puck puck3 = new Puck(rink.playerRight.getX() - rink.playerRight.getRadius() - 10f, rink.playerRight.getY(), 10f, 0f, 10f, "testPuck3", rink);
        rink.pucks.add(puck3);
        Assertions.assertTrue(puck3.isCollidingWith(rink.playerRight),
                "A puck touching the player should immediately count as colliding with them.");


    }

    @Test
    @DisplayName("Collisions with another puck")
    public void testCollisionWithPuck() {
        Rink rink = new Rink(1000, 600);
        Puck puck1 = rink.pucks.get(0);
        puck1.setVy(0f);
        puck1.setVx(0f);

        Puck puck2 = new Puck(puck1.getX() + puck1.getRadius() + 10.0001f, puck1.getY(), -100f, 0f, 10f, "testPuck2", rink);
        rink.pucks.add(puck2);
        Assertions.assertFalse(puck2.isCollidingWith(puck1),
                "Place a puck close but not touching puck");

        rink.tick();
        Assertions.assertTrue(puck2.isCollidingWith(puck1),
                "Move puck towards the other. They should now be colliding");

        Puck puck3 = new Puck(puck1.getX() - puck1.getRadius() - 10f, puck1.getY(), 10f, 0f, 10f, "testPuck3", rink);
        rink.pucks.add(puck3);
        Assertions.assertTrue(puck3.isCollidingWith(puck1),
                "A puck touching the puck should immediately as colliding with them.");
    }

    private float getEnergy(Puck puck) {
        return 0.5f * puck.getMass() * (puck.getVx() * puck.getVx() + puck.getVy() * puck.getVy());
    }

    @Test
    @DisplayName("Conservation of energy")
    public void conservationOfEnergy() {
        // Head on collision
        Rink rink = new Rink(1000, 600);
        Puck puck1 = rink.pucks.get(0);
        puck1.setVy(0f);
        puck1.setVx(0f);

        Puck puck2 = new Puck(puck1.getX() + puck1.getRadius() + 10f, puck1.getY(), -10f, 0f, 10f, "testPuck", rink);
        rink.pucks.add(puck2);

        float energy = getEnergy(puck1) + getEnergy(puck2);
        rink.tick();
        Assertions.assertEquals(energy, getEnergy(puck1) + getEnergy(puck2),
                "Kinetic energy in a perfect \"Head on\" elastic collision with no friction should stay the same.");

        // Angled collision
        rink = new Rink(1000, 600);
        puck1 = rink.pucks.get(0);
        puck1.setVy(2f);
        puck1.setVx(0f);

        puck2 = new Puck(puck1.getX() + puck1.getRadius() + 8f, puck1.getY() + 5f, -10f, 0f, 10f, "testPuck", rink);
        rink.pucks.add(puck2);

        energy = getEnergy(puck1) + getEnergy(puck2);
        rink.tick();

        Assertions.assertEquals(energy, getEnergy(puck1) + getEnergy(puck2),
                "Kinetic energy in a perfect \"Head on\" elastic collision with no friction should stay the same.");

    }

}
