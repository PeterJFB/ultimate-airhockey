package airhockey.environment;

import javafx.scene.shape.Circle;

class Puck implements circleObject {

    // Position
    private float x;
    private float y;
    private float radius;

    // Movement
    private float vx; // px per s
    private float vy; // px per s
    private final float dt; // s

    // Physics
    private final float mass = 10;
    private final float SPAWN_VELOCITY = 10f; // px per s

    // Other
    private final Rink rink;
    private String lastCollidedWith = "";
    private String id;

    public Puck(float x, float y, float vx, float vy, float radius, String id, Rink rink) {
        this.rink = rink;

        setRadius(radius);

        setX(x);
        setY(y);
        setVx(vx);
        setVy(vy);

        setId( id.isEmpty() ? String.valueOf(this.hashCode()) : id);
        dt = rink.getTickInterval() / 1000f;
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

    public float getMass() {
        return mass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id of puck cannot be null.");
        }
        this.id = id;
    }

    // Logic

    public void updateWallCollision() {
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

    private float getDistanceSquared(circleObject object) {
        float dx = object.getX() - getX();
        float dy = object.getY() - getY();

        return dx * dx + dy * dy;
    }

    public boolean isCollidingWith(Player player) {
        float distanceSquared = getDistanceSquared(player);
        float dr = player.getRadius() + getRadius();

        if (distanceSquared <= dr * dr) {
            return !getLastCollidedWith().equals(player.getId());
        }

        // Change last collision if they no longer are close to each other
        if (getLastCollidedWith().equals(player.getId()) && distanceSquared >= dr * dr * 2)
            setLastCollidedWith("");

        return false;
    }

    public boolean isCollidingWith(Puck puck) {
        float distanceSquared = getDistanceSquared(puck);
        float dr = puck.getRadius() + getRadius();

        if (distanceSquared <= dr * dr) {
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
        float newCollVx = ((getMass() - player.getMass()) * collVx + (2 * player.getMass() * playerCollVx)) / (getMass() + player.getMass());
        float newCollVy = ((getMass() - player.getMass()) * collVy + (2 * player.getMass() * playerCollVy)) / (getMass() + player.getMass());

        // Add new velocity to puck
        setVx(newCollVx + tanVx);
        setVy(newCollVy + tanVy);

        // Logic
        setLastCollidedWith(player.getId());

        // I shall now retire
    }

    public void performCollisionWith(Puck puck) {

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
        float newCollVx = ((getMass() - puck.getMass()) * collVx + (2 * puck.getMass() * puckCollVx)) / (getMass() + puck.getMass());
        float newCollVy = ((getMass() - puck.getMass()) * collVy + (2 * puck.getMass() * puckCollVy)) / (getMass() + puck.getMass());

        float puckNewCollVx = ((puck.getMass() - getMass()) * puckCollVx + (2 * getMass() * collVx)) / (getMass() + puck.getMass());
        float puckNewCollVy = ((puck.getMass() - getMass()) * puckCollVy + (2 * getMass() * collVy)) / (getMass() + puck.getMass());


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
        setVx((float) Math.sin(Math.random() * 2f * Math.PI) * SPAWN_VELOCITY);
        setVy((float) Math.sin(Math.random() * 2f * Math.PI) * SPAWN_VELOCITY);
    }

    public void resetTo(Player player) {
        resetVel();

        setY(rink.getHeight() / 2f);
        switch (player.getSide()) {
            case LEFT -> setX(11/50f * rink.getWidth());
            case RIGHT -> setX(rink.getWidth() - 11/50f * rink.getWidth());
        }

        setLastCollidedWith(player.getId());
    }

    // Drawing

    public Circle draw() {

        Circle puckCircle = new Circle();
        puckCircle.setRadius(getRadius());
        puckCircle.setCenterX(getX());
        puckCircle.setCenterY(getY());

        return puckCircle;
    }
}
