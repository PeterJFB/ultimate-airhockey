package airhockey.environment;

import javafx.scene.CacheHint;
import javafx.scene.shape.Circle;

class Puck implements DiskObject {

    // Position
    private float x;
    private float y;
    private float radius;

    // Movement
    private float vx; // px per s
    private float vy; // px per s
    private final float dt; // s

    // Physics
    private final float MASS = 10;
    private final float SPAWN_VELOCITY = 10f; // px per s
    private final float SPAWN_FACTOR = 11/50f;

    // Other
    private final Rink rink;
    private String lastCollidedWith = "";
    private String id;
    private Circle puckCircle;

    public Puck(float x, float y, float vx, float vy, float radius, String id, Rink rink) {
        if (rink == null) {
            throw new IllegalArgumentException("rink cannot be null.");
        }
        this.rink = rink;

        setRadius(radius);

        setX(x);
        setY(y);
        setVx(vx);
        setVy(vy);

        setId( id == null || id.isEmpty() ? String.valueOf(this.hashCode()) : id);
        dt = rink.getTickInterval() / 1000f;

        createPuckCircle();
    }

    public Puck(float x, float y, float vx, float vy, float radius, Rink rink) {
        this(x, y, vx, vy, radius, "", rink);
    }


    public Puck(float x, float y, float radius, Rink rink) {
        this(x, y, 0, 0, radius, rink);
    }

    public Puck(float radius, Rink rink) {
        this(rink.getWidth()/2f, rink.getHeight()/2f, 0, 0, radius, rink);
        resetTo(rink.playerLeft);
    }

    // Getters and Setters

    public float getX() {
        return x;
    }

    private void setX(float x) {
        if (x < 0 || rink.getWidth() < x)
            throw new IllegalArgumentException("Invalid x-position of Puck: " + x);
        this.x = x;
    }

    public float getY() {
        return y;
    }

    private void setY(float y) {
        if (y < 0 || rink.getHeight() < y)
            throw new IllegalArgumentException("Invalid y-position of Puck: " + y);
        this.y = y;
    }

    public float getVx() {
        return vx;
    }

    void setVx(float vx) {
        if (Math.abs(vx) > rink.getWidth()/dt) {
            throw new IllegalArgumentException("Velocity is unreasonably large: " + vx);
        }
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    void setVy(float vy) {
        if (Math.abs(vy) > rink.getHeight()/dt) {
            throw new IllegalArgumentException("Velocity is unreasonably large: " + vy);
        }
        this.vy = vy;
    }

    public float getRadius() {
        return radius;
    }

    void setRadius(float radius) {
        if (radius <= 0)
            throw new IllegalArgumentException("Radius must be a positive number: " + radius);
        this.radius = radius;
    }

    public void setLastCollidedWith(String lastCollidedWith) {
        this.lastCollidedWith = lastCollidedWith;
    }

    public String getLastCollidedWith() {
        return lastCollidedWith;
    }

    public float getMASS() {
        return MASS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id of puck cannot be null or blank.");
        }
        this.id = id;
    }

    // Logic

    public void updateWallCollision() {
        // Inverts velocity if the puck is touching a wall
        if (getX() - getRadius() <= 0) {
            setVx(Math.abs(getVx()));
        } else if (rink.getWidth() <= getX() + getRadius()) {
            setVx(-Math.abs(getVx()));
        }
        if (getY() - getRadius() <= 0) {
            setVy(Math.abs(getVy()));
        } else if (rink.getHeight() <= getY() + getRadius()) {
            setVy(-Math.abs(getVy()));
        }
    }

    private float getDistanceSquared(DiskObject object) {
        if (object == null) {
            throw new IllegalArgumentException("object cannot be null.");
        }

        float dx = object.getX() - getX();
        float dy = object.getY() - getY();

        return dx * dx + dy * dy;
    }

    public boolean isCollidingWith(Player player) {
        if (player == null)
            return false;

        float distanceSquared = getDistanceSquared(player);
        float dr = player.getRadius() + getRadius();

        if (distanceSquared <= dr * dr) {
            // The collision is only performed if they have not just collided with each other
            return !getLastCollidedWith().equals(player.getId());
        }

        // Change last collision if they no longer are close to each other
        if (getLastCollidedWith().equals(player.getId()) && distanceSquared >= dr * dr * 2)
            setLastCollidedWith("");

        return false;
    }

    public boolean isCollidingWith(Puck puck) {
        if (puck == null)
            return false;

        float distanceSquared = getDistanceSquared(puck);
        float dr = puck.getRadius() + getRadius();

        if (distanceSquared <= dr * dr) {
            // The collision is only performed if they have not just collided with each other
            return !getLastCollidedWith().equals(puck.getId()) || !puck.getLastCollidedWith().equals(getId());
        }

        // Change last collision if they no longer are close to each other
        if (distanceSquared >= dr * dr * 2) {
            if (getLastCollidedWith().equals(puck.getId()))
                setLastCollidedWith("");

            if (puck.getLastCollidedWith().equals(getId()))
                puck.setLastCollidedWith("");
        }

        return false;
    }

    public void performCollisionWith(Player player) {
        if (player == null)
            return;

        // Get direction of collision
        float dirX = player.getX() - getX();
        float dirY = player.getY() - getY();

        // Get velocity in collision direction
        float collConstant = (dirX * getVx() + dirY * getVy()) / (dirX * dirX + dirY * dirY);
        float collVx = collConstant * dirX;
        float collVy = collConstant * dirY;


        float playerCollConstant = (-dirX * player.getVx() + -dirY * player.getVy()) / (dirX * dirX + dirY * dirY);
        float playerCollVx = playerCollConstant * -dirX;
        float playerCollVy = playerCollConstant * -dirY;

        // Get velocity of puck tangent to collision direction. This will not change
        float tanVx = getVx() - collVx;
        float tanVy = getVy() - collVy;

        // New velocity from elastic collision
        float newCollVx = ((getMASS() - player.getMASS()) * collVx + (2 * player.getMASS() * playerCollVx)) / (getMASS() + player.getMASS());
        float newCollVy = ((getMASS() - player.getMASS()) * collVy + (2 * player.getMASS() * playerCollVy)) / (getMASS() + player.getMASS());

        // Add new velocity to puck
        setVx(newCollVx + tanVx);
        setVy(newCollVy + tanVy);

        // Logic
        setLastCollidedWith(player.getId());

        // I shall now retire
    }

    public void performCollisionWith(Puck puck) {
        if (puck == null)
            return;

        // Get direction of collision
        float dirX = puck.getX() - getX();
        float dirY = puck.getY() - getY();

        // Get velocity in collision direction
        float collConstant = (dirX * getVx() + dirY * getVy()) / (dirX * dirX + dirY * dirY);
        float collVx = collConstant * dirX;
        float collVy = collConstant * dirY;


        float puckCollConstant = (-dirX * puck.getVx() + -dirY * puck.getVy()) / (dirX * dirX + dirY * dirY);
        float puckCollVx = puckCollConstant * -dirX;
        float puckCollVy = puckCollConstant * -dirY;

        // Get velocity of puck tangent to collision direction. This will not change
        float tanVx = getVx() - collVx;
        float tanVy = getVy() - collVy;

        float puckTanVx = puck.getVx() - puckCollVx;
        float puckTanVy = puck.getVy() - puckCollVy;

        // New velocity from elastic collision
        float newCollVx = ((getMASS() - puck.getMASS()) * collVx + (2 * puck.getMASS() * puckCollVx)) / (getMASS() + puck.getMASS());
        float newCollVy = ((getMASS() - puck.getMASS()) * collVy + (2 * puck.getMASS() * puckCollVy)) / (getMASS() + puck.getMASS());

        float puckNewCollVx = ((puck.getMASS() - getMASS()) * puckCollVx + (2 * getMASS() * collVx)) / (getMASS() + puck.getMASS());
        float puckNewCollVy = ((puck.getMASS() - getMASS()) * puckCollVy + (2 * getMASS() * collVy)) / (getMASS() + puck.getMASS());


        // Add new velocity to puck
        setVx(newCollVx + tanVx);
        setVy(newCollVy + tanVy);

        puck.setVx(puckNewCollVx + puckTanVx);
        puck.setVy(puckNewCollVy + puckTanVy);

        // Logic
        setLastCollidedWith(puck.getId());

        puck.setLastCollidedWith(getId());

        // I shall now retire

    }

    public void moveForward() {
        float newX = getX() + getVx() * dt;
        newX = newX < 0 ? 0 : newX > rink.getWidth() ? rink.getWidth() : newX;
        setX(newX);
        float newY = getY() + getVy() * dt;
        newY = newY < 0 ? 0 : newY > rink.getHeight() ? rink.getHeight() : newY;
        setY(newY);
    }

    void resetVel() {
        // Spawn-velocity is set in a random direction to ensure variety.
        setVx((float) Math.cos(Math.random() * 2f * Math.PI) * SPAWN_VELOCITY);
        setVy((float) Math.sin(Math.random() * 2f * Math.PI) * SPAWN_VELOCITY);
    }

    public void resetTo(Player player) {
        if (player == null)
            throw new IllegalArgumentException("player cannot be null.");

        resetVel();

        setY(rink.getHeight() / 2f);
        switch (player.getSide()) {
            case LEFT -> setX(SPAWN_FACTOR * rink.getWidth());
            case RIGHT -> setX(rink.getWidth() - SPAWN_FACTOR * rink.getWidth());
        }

        // Ensure that resetting position does not lead to instant collision with player
        setLastCollidedWith(player.getId());
    }

    // Drawing

    private void createPuckCircle() {
        puckCircle = new Circle();
        puckCircle.setRadius(getRadius());
        puckCircle.getStyleClass().add("puck");
        // https://stackoverflow.com/questions/18911186/how-do-setcache-and-cachehint-work-together-in-javafx
        puckCircle.cacheProperty().set(true);
        puckCircle.cacheHintProperty().set(CacheHint.SPEED);
    }

    public Circle draw() {
        puckCircle.setCenterX(getX());
        puckCircle.setCenterY(getY());

        return puckCircle;
    }
}
