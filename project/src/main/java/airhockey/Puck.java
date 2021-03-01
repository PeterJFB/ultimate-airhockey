package airhockey;


/*
* TODO:
* Check collision with other pucks
*
*
*
* */


import javafx.scene.shape.Circle;

public class Puck {

    // Position
    private float x;
    private float y;
    private float radius;

    // Movement
    private float vx;
    private float vy;
    private float dt;

    // Physics
    final private float mass = 10;

    // Other
    private Rink rink;
    private GoalSide lastCollidedWith;

    public Puck(float x, float y, float vx, float vy, Rink rink, float radius) {
        this.rink = rink;
        setX(x);
        setY(y);
        setVx(vx);
        setVy(vy);
        setRadius(radius);
        dt = (float) rink.getTickInterval()/1000;
    }

    public Puck(float x, float y, Rink rink, float radius) {
        this(x, y, 0, 0, rink, radius);
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

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        if (radius <= 0)
            throw new IllegalArgumentException("Radius must be a positive number: " + radius);
        this.radius = radius;
    }

    public void setLastCollidedWith(GoalSide lastCollidedWith) {
        this.lastCollidedWith = lastCollidedWith;
    }

    public GoalSide getLastCollidedWith() {
        return lastCollidedWith;
    }

    // Logic

    public void updateWallCollision() {
        if (x - radius <= 0) {
            setVx(Math.abs(vx));
        } else if (rink.getWidth() <= x + radius) {
            setVx(-Math.abs(vx));
        }
        if (y - radius <= 0) {
            setVy(Math.abs(vy));
        } else if (rink.getHeight() <= y + radius) {
            setVy(-Math.abs(vy));
        }
    }

    public boolean isCollidingWith(Player player) {
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float dr = player.getRadius() + radius;

        if (dx*dx + dy*dy <= dr*dr) {
            return getLastCollidedWith() != player.getSide();
        }

        // Change last collision if they no longer are close to each other
        if (getLastCollidedWith() == player.getSide() && dx*dx + dy*dy >= dr*dr*2)
            setLastCollidedWith(null);

        return false;
    }

    public void performCollisionWith(Player player) {

        System.out.println(vx + " " + vy);
        // Get direction of collision
        float dirX = player.getX()-x;
        float dirY = player.getY()-y;

        // Get velocity in collision direction
        float collConstant = (dirX * vx + dirY * vy) / (dirX*dirX + dirY*dirY);
        float collVx = collConstant * dirX;
        float collVy = collConstant * dirY;


        float playerCollConstant = (-dirX * player.getVx() + -dirY * player.getVy()) / (dirX*dirX + dirY*dirY);
        float playerCollVx = playerCollConstant * -dirX;
        float playerCollVy = playerCollConstant * -dirY;

        // Get velocity of puck tangent to collision direction. This will not change
        float tanVx = vx - collVx;
        float tanVy = vy - collVy;

        // New velocity from elastic collision
        System.out.println(player.mass);
        float newCollVx = ((mass - player.mass) * collVx + (2 * player.mass * playerCollVx)) / (mass + player.mass);
        float newCollVy = ((mass - player.mass) * collVy + (2 * player.mass * playerCollVy)) / (mass + player.mass);

        // Add new velocity to puck
        setVx(newCollVx+tanVx);
        setVy(newCollVy+tanVy);

        // Logic
        setLastCollidedWith(player.getSide());

        System.out.println(vx + " " + vy);
        // I shall now retire

    }

    public void moveForward() {
        float newX = x + vx * dt;
        newX = newX < 0 ? 0 : newX > rink.getWidth() ? rink.getWidth() : newX;
        setX(newX);
        float newY = y + vy * dt;
        newY = newY < 0 ? 0 : newY > rink.getHeight() ? rink.getHeight() : newY;
        setY(newY);
    }

    public void resetTo(GoalSide side) {
        vx = 0;
        vy = 0;

        y = rink.getHeight()/2f;
        switch (side) {
            case LEFT -> x = 110;
            case RIGHT -> x = rink.getWidth() - 110;
        }

        setLastCollidedWith(side);
    }

    // Drawing

    public Circle draw() {

        Circle puckCircle = new Circle();
        puckCircle.setRadius(radius);
        puckCircle.setCenterX(x);
        puckCircle.setCenterY(y);
        puckCircle.setStyle("-fx-fill: black;");

        return puckCircle;
    }

}
