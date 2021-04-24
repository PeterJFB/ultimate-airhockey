package airhockey.environment;

import javafx.scene.CacheHint;
import javafx.scene.shape.Circle;

class Player extends PlayerControls implements DiskObject {

    // Position
    private float x;
    private float y;
    private final float originX;
    private final float originY;
    private float reachX;
    private float reachY;
    private float radius = 24;

    // Movement
    private float vx = 0;
    private float vy = 0;
    private final float DEFAULT_VELOCITY = 400;
    private final float dt;

    // Physics
    private final float MASS = 20;

    // Other
    private final Side side;
    private String name;
    private final Rink rink;
    private Circle playerCircle;

    public Player(float x, float y,
                  float vx, float vy,
                  float originX,
                  float reachX, float reachY,
                  float radius,
                  Side side, String name, Rink rink) {
        super();
        if (rink == null) {
            throw new IllegalArgumentException("rink cannot be null.");
        }
        this.rink = rink;

        setRadius(radius);

        setX(x);
        setY(y);
        setVx(vx);
        setVy(vy);

        validateOriginX(originX);
        this.originX = originX;
        this.originY = rink.getHeight() / 2f;

        setReachX(reachX);
        setReachY(reachY);

        dt = (float) this.rink.getTickInterval() / 1000;
        if (side == null) {
            throw new IllegalArgumentException("side cannot be null.");
        }
        this.side = side;
        setName(name != null ? name : (side == Side.LEFT ? "Player 1" : "Player 2"));

        createPlayerCircle();
    }

    public Player(float x, Rink rink, float reachX, float radius, Side side, String name) {
        this(x, rink.getHeight() / 2f,
                0, 0, x,
                reachX, rink.getHeight() / 2f - radius,
                radius, side, name, rink);
    }

// Getters and Setters

    public float getRadius() {
        return radius;
    }

    private void setRadius(float radius) {
        if (radius <= 0)
            throw new IllegalArgumentException("Radius must be a positive number: " + radius);
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (0 + radius > x || x > rink.getWidth() - radius)
            throw new IllegalArgumentException("X must be within bounds of rink (0 - %s): %s".formatted(rink.getWidth(), x));
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (0 + radius > y || y > rink.getHeight() - radius)
            throw new IllegalArgumentException("Y must be within bounds of rink (0 - %s): %s".formatted(rink.getHeight(), y));
        this.y = y;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        if (Math.abs(vx) > rink.getWidth())
            throw new IllegalArgumentException("Vx is larger than size of rink: " + vx);
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        if (Math.abs(vx) > rink.getHeight())
            throw new IllegalArgumentException("Vy is larger than size of rink: " + vy);
        this.vy = vy;
    }

    public float getOriginX() {
        return originX;
    }

    private void validateOriginX(float originX) {
        if (0 + radius > originX || originX > rink.getWidth() - radius) {
            throw new IllegalArgumentException("OriginX must be within bounds of rink (0 - %s): %s".formatted(rink.getWidth(), originX));
        }
    }

    private float getOriginY() {
        return originY;
    }

    public float getReachX() {
        return reachX;
    }

    public void setReachX(float reachX) {
        if (reachX <= 0)
            throw new IllegalArgumentException("reachX has to be positive: " + reachX);
        this.reachX = reachX;
    }

    public float getReachY() {
        return reachY;
    }

    public void setReachY(float reachY) {
        if (reachY <= 0)
            throw new IllegalArgumentException("reachY has to be positive: " + reachY);
        this.reachY = reachY;
    }

    public Side getSide() {
        return side;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("name cannot be null or empty.");
        this.name = name;
    }

    public float getMASS() {
        return MASS;
    }

    public String getId() {
        return getName();
    }

    // Movement

    /*
     * There has been written no tests for this code, as a lot of this is highly customized for this game, and
     * might change at any time. They've instead been through a process of game testing and adjusting to make sure they
     * FEEL as intended.
     * Worst error that can arise here i a division by zero, but in every case where this can happen, exists an if-clause
     * right before to negate the issue.
     * */


    public void updateMovement() {

        // Update velocity based on input

        if (isPressingUp()) {
            moveUp();
        }
        if (isPressingDown()) {
            moveDown();
        }
        if (isPressingLeft() && side == Side.RIGHT) {
            moveLeft();
        }
        if (isPressingRight() && side == Side.LEFT) {
            moveRight();
        }

        // Update position based on which direction is pressed 

        // Vertical
        if (isPressingUp() || isPressingDown()) {
            y += getVy() * dt;
        } else {
            // If no vertical input, move player towards origin
            float centerDirX = getOriginX() - getX();
            float centerDirY = getOriginY() - getY();

            if (Math.abs(centerDirX) > DEFAULT_VELOCITY * dt || Math.abs(centerDirY) > DEFAULT_VELOCITY * dt) {
                float scale = (float) (DEFAULT_VELOCITY / Math.sqrt(centerDirX * centerDirX + centerDirY * centerDirY));
                vy = centerDirY * scale;
                y += getVy() * dt;
            } else {
                y = getOriginY();
                vy = 0;
            }
        }
        // Adjust position if player is exceeding their vertical reach
        if (Math.abs(y - getOriginY()) > getReachY()) {
            setY(y - getOriginY() < 0 ? getOriginY() - getReachY() : getOriginY() + getReachY());
        }

        // Horizontal
        if (isPressingLeft() && side == Side.RIGHT || isPressingRight() && side == Side.LEFT) {
            x += getVx() * dt;
        } else {
            // If no horizontal input, move player towards origin
            float centerDirX = getOriginX() - getX();
            float centerDirY = getOriginY() - getY();

            if (Math.abs(centerDirX) > DEFAULT_VELOCITY * dt || Math.abs(centerDirY) > DEFAULT_VELOCITY * dt) {
                float scale = (float) (DEFAULT_VELOCITY / Math.sqrt(centerDirX * centerDirX + centerDirY * centerDirY));
                vx = centerDirX * scale;
                x += getVx() * dt;
            } else {
                x = getOriginX();
                vx = 0;
            }
        }
        // Adjust position if player is exceeding their Horizontal reach
        if (Math.abs(x - getOriginX()) > getReachX()) {
            setX(x - getOriginX() < 0 ? getOriginX() - getReachX() : getOriginX() + getReachX());
        }
    }

    private void moveUp() {
        vy = -DEFAULT_VELOCITY;
    }

    private void moveDown() {
        vy = DEFAULT_VELOCITY;
    }

    // Horizontal velocity is based on how far the player is from origin
    private void moveLeft() {
        vx = -DEFAULT_VELOCITY * getVxDistanceMultiplier();
    }

    private void moveRight() {
        vx = DEFAULT_VELOCITY * getVxDistanceMultiplier();
    }

    private float getVxDistanceMultiplier() {
        return (float) Math.pow((4 * getReachX() - Math.abs(getX() - getOriginX())) / (4 * getReachX()), 2);
    }

    // Drawing

    private void createPlayerCircle() {
        playerCircle = new Circle();
        playerCircle.setRadius(getRadius());
        playerCircle.getStyleClass().add(getSide() == Side.LEFT ? "playerLeft" : "playerRight");
        // https://stackoverflow.com/questions/18911186/how-do-setcache-and-cachehint-work-together-in-javafx
        playerCircle.cacheProperty().set(true);
        playerCircle.cacheHintProperty().set(CacheHint.SPEED);
    }

    public Circle draw() {
        playerCircle.setCenterX(getX());
        playerCircle.setCenterY(getY());

        return playerCircle;
    }
}
