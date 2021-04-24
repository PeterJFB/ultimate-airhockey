package airhockey.environment;

import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

/*
* RandomPuckSpawner is a delegate used by Rink, and is created whenever the Rink wants to spawn Puck at a random position.
* */

class RandomPuckSpawner implements DiskObject {

    // Position
    private final float RADIUS = 18;
    private final float x;
    private final float y;

    // Logic
    private final float initialSpawnTime; // s
    private final float SPAWN_MARGIN = RADIUS * 2f;
    private boolean finished = false;

    private CountDown countDown;
    
    // Other
    private final Rink rink;
    private Arc spawnArc;

    public RandomPuckSpawner(Rink rink, int spawnTime) {
        if (rink == null)
            throw new IllegalArgumentException("rink cannot be null.");
        this.rink = rink;

        this.x = ranRange(SPAWN_MARGIN, rink.getWidth() - SPAWN_MARGIN);
        this.y = ranRange(SPAWN_MARGIN, rink.getHeight() - SPAWN_MARGIN);

        // We do no exception check, as it is done in CountDown
        initialSpawnTime = spawnTime;
        countDown = new CountDown((int) initialSpawnTime, rink.getTickInterval() / 1000f);

        createSpawnArc();
    }

    // Getters and Setters

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getRadius() {
        return RADIUS;
    }

    public boolean isFinished() {
        return finished;
    }

    // Logic

    private boolean isCollidingWith(DiskObject object) {
        if (object == null)
            throw new IllegalArgumentException("object cannot be null.");

        float dx = object.getX() - getX();
        float dy = object.getY() - getY();

        float dr = (getRadius() + object.getRadius()) * 1.5f;

        return dx * dx + dy * dy < dr * dr;
    }

    private float ranRange(float min, float max) {
        return (float) Math.random() * (max - min) + min;
    }

    public void tick() {
    	countDown.tick();
        // Attempt to spawn if it is finished
        if (countDown.isFinished()) {

            // Check for collision
            for (Puck puck : rink.pucks) {
                if (isCollidingWith(puck)) {
                    return;
                }
            }
            if (isCollidingWith(rink.playerLeft) || isCollidingWith(rink.playerRight)) {
                return;
            }
            Puck newPuck = new Puck(x, y, RADIUS, rink);
            newPuck.resetVel();
            rink.pucks.add(newPuck);
            finished = true;
        }
    }

    // Drawing

    private void createSpawnArc() {
        spawnArc = new Arc();
        spawnArc.setRadiusX(getRadius());
        spawnArc.setRadiusY(getRadius());
        spawnArc.setCenterX(getX());
        spawnArc.setCenterY(getY());
        spawnArc.setStartAngle(90f);
        spawnArc.setType(ArcType.ROUND);
        spawnArc.getStyleClass().add("puckSpawner");
    }

    public Arc draw() {
        spawnArc.setLength((countDown.getInitialTime() - countDown.getTime()) / countDown.getInitialTime() * 360f);
        return spawnArc;
    }
}
