package airhockey.environment;

import javafx.scene.shape.Circle;

class Player extends PlayerControls implements circleObject {

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
    private final float VELOCITY = 400;
    private final float dt;

    // Physics
    public final float mass = 20;

    // Other
    private final Side side;
    private String name;
    private final Rink rink;

    public Player(float x, float y,
                  float vx, float vy,
                  float originX,
                  float reachX, float reachY,
                  float radius,
                  Side side, String name, Rink rink) {
        super();
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
        this.side = side;
        this.name = name;
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
        if (radius < 0)
            throw new IllegalArgumentException("Radius cannot be negative: " + radius);
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (0 + radius > x || x > rink.getWidth() - radius)
            throw new IllegalArgumentException("X must be within bounds of rink (0 - " + rink.getWidth() + "): " + x);
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (0 + radius > y || y > rink.getHeight() - radius)
            throw new IllegalArgumentException("Y must be within bounds of rink (0 - " + rink.getHeight() + "): " + y);
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

    public void validateOriginX(float originX) {
        if (0 + radius > originX || originX > rink.getWidth() - radius) {
            throw new IllegalArgumentException("OriginX must be within bounds of rink (0 - " + rink.getWidth() + "): " + originX);
        }
    }

    private float getOriginY() {
        return originY;
    }

    public float getReachX() {
        return reachX;
    }

    public void setReachX(float reachX) {
        if (reachX < 0)
            throw new IllegalArgumentException("reachX has to be positive: " + reachX);
        this.reachX = reachX;
    }

    public float getReachY() {
        return reachY;
    }

    public void setReachY(float reachY) {
        if (reachY < 0)
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
        this.name = name;
    }

    public float getMass() {
        return mass;
    }

    private void validateMass(float mass) {
        if (mass <= 0) {
            throw new IllegalArgumentException("Mass has to be positive: " + mass);
        }
    }

    public String getId() {
        return getName();
    }

    // Movement

    public void updateMovement() {

        // Update velocity based on input

        if (pressingUp) {
            moveUp();
        }
        if (pressingDown) {
            moveDown();
        }
        if (pressingLeft && side == Side.RIGHT) {
            moveLeft();
        }
        if (pressingRight && side == Side.LEFT) {
            moveRight();
        }

        // Update position based on which direction is pressed 

        // Vertical
        if (pressingUp || pressingDown) {
            y += getVy() * dt;
        } else {
            // If no vertical input, move player towards origin
            float centerDirX = getOriginX() - getX();
            float centerDirY = getOriginY() - getY();

            if (Math.abs(centerDirX) > VELOCITY * dt || Math.abs(centerDirY) > VELOCITY * dt) {
                float scale = (float) (VELOCITY / Math.sqrt(centerDirX * centerDirX + centerDirY * centerDirY));
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
        if (pressingLeft && side == Side.RIGHT || pressingRight && side == Side.LEFT) {
            x += getVx() * dt;
        } else {
            // If no horizontal input, move player towards origin
            float centerDirX = getOriginX() - getX();
            float centerDirY = getOriginY() - getY();

            if (Math.abs(centerDirX) > VELOCITY * dt || Math.abs(centerDirY) > VELOCITY * dt) {
                float scale = (float) (VELOCITY / Math.sqrt(centerDirX * centerDirX + centerDirY * centerDirY));
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
        vy = -VELOCITY;
    }

    private void moveDown() {
        vy = VELOCITY;
    }

    // Horizontal velocity is based on how far the player is from origin
    private void moveLeft() {
        vx = -VELOCITY * getVxDistanceMultiplier();
    }

    private void moveRight() {
        vx = VELOCITY * getVxDistanceMultiplier();
    }

    private float getVxDistanceMultiplier() {
        return (float) Math.pow((4 * getReachX() - Math.abs(getX() - getOriginX())) / (4 * getReachX()), 2);
    }

    // Drawing

    public Circle draw() {
        Circle playerCircle = new Circle();
        playerCircle.setRadius(radius);
        playerCircle.setCenterX(x);
        playerCircle.setCenterY(y);
        playerCircle.getStyleClass().add("player");

        return playerCircle;
    }
}
