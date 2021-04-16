package airhockey.environment;

import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

class PuckSpawner implements diskObject {

    // Position
    private final float radius = 18;
    private final float x;
    private final float y;

    // Logic
    private float spawnTime; // s
    private final float initialSpawnTime; // s
    private final float spawnMargin = radius * 2;
    private boolean finished = false;

    // Other
    private final Rink rink;
// TODO: Refactor to use countdown?
    public PuckSpawner(Rink rink, int spawnTime) {
        this.rink = rink;
        this.x = ranRange(spawnMargin, rink.getWidth() - spawnMargin);
        this.y = ranRange(spawnMargin, rink.getHeight() - spawnMargin);
        this.initialSpawnTime = spawnTime;
        this.spawnTime = initialSpawnTime;
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
        return radius;
    }

    public boolean isFinished() {
        return finished;
    }

    // Logic

    private boolean isCollidingWith(diskObject object) {
        float dx = object.getX() - getX();
        float dy = object.getY() - getY();

        float dr = (getRadius() + object.getRadius()) * 1.5f;

        return dx * dx + dy * dy < dr * dr;
    }

    private float ranRange(float min, float max) {
        return (float) Math.random() * (max - min) + min;
    }

    public void tick() {
        spawnTime -= (float) rink.getTickInterval() / 1000f;
        if (spawnTime <= 0) {
            spawnTime = 0;

            // Check for collision
            for (Puck puck : rink.pucks) {
                if (isCollidingWith(puck)) {
                    return;
                }
            }
            if (isCollidingWith(rink.playerLeft) || isCollidingWith(rink.playerRight)) {
                return;
            }
            Puck newPuck = new Puck(x, y, radius, rink);
            newPuck.resetVel();
            rink.pucks.add(newPuck);
            finished = true;

        }
    }

    // Drawing

    public Arc draw() {
        Arc spawnArc = new Arc();
        spawnArc.setCenterX(getX());
        spawnArc.setCenterY(getY());
        spawnArc.setRadiusX(getRadius());
        spawnArc.setRadiusY(getRadius());
        spawnArc.setStartAngle(90f);
        spawnArc.setType(ArcType.ROUND);
        spawnArc.setLength((initialSpawnTime - spawnTime) / initialSpawnTime * 360f);
        spawnArc.getStyleClass().add("puckSpawner");

        return spawnArc;
    }
}
