package airhockey;

import javafx.scene.shape.Circle;

public class Player extends PlayerControls {

    // Position
    private float x;
    private float y;
    private final float originX;
    private final float originY;
    private float reachX;
    private float reachY;
    private float radius = 12;

    // Movement
    private float vx = 0;
    private float vy = 0;
    private float v = 200;
    private float dt;

    // Physics
    final public float mass = 20;

    // Other
    private GoalSide side;
    private String name;
    private Rink rink;

    public Player(float x, Rink rink, float reachX, GoalSide side, String name) {
        super();
        this.rink = rink;
        originY = (float) rink.getHeight() / 2;
        originX = x;
        this.x = originX;
        this.y = originY;
        this.reachX = reachX;
        this.reachY = rink.getHeight()/2f - radius;

        dt = (float) this.rink.getTickInterval() / 1000;

        this.side = side;
        this.name = name;
    }

    // Getters and Setters

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public float getRadius() {
        return radius;
    }

    public GoalSide getSide() {
        return side;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (pressingLeft && side == GoalSide.RIGHT) {
            moveLeft();
        }
        if (pressingRight && side == GoalSide.LEFT) {
            moveRight();
        }
        
        // Update position based on which direction is pressed 

        // Vertical
        if (pressingUp || pressingDown) {
            y += vy * dt;
        } else {
            // If no vertical input, move player towards origin
            float centerDirX = originX - x;
            float centerDirY = originY - y;

            if (Math.abs(centerDirX) > v*dt || Math.abs(centerDirY) > v*dt) {
                float scale = (float) (v / Math.sqrt(centerDirX * centerDirX + centerDirY * centerDirY));
                vy = centerDirY * scale;
                y += vy * dt;
            } else {
                y = originY;
                vy = 0;
            }
        }
        // Adjust position if player is exceeding their vertical reach
        if (Math.abs(y-originY) > reachY) {
            y = y - originY < 0 ? originY - reachY : originY + reachY;
        }

        // Horizontal
        if (pressingLeft && side == GoalSide.RIGHT || pressingRight && side == GoalSide.LEFT) {
            x += vx * dt;
        } else {
            // If no horizontal input, move player towards origin
            float centerDirX = originX - x;
            float centerDirY = originY - y;

            if (Math.abs(centerDirX) > v*dt || Math.abs(centerDirY) > v*dt) {
                float scale = (float) (v / Math.sqrt(centerDirX * centerDirX + centerDirY * centerDirY));
                vx = centerDirX * scale;
                x += vx * dt;
            } else {
                x = originX;
                vx = 0;
            }
        }
        // Adjust position if player is exceeding their Horizontal reach
        if (Math.abs(x-originX) > reachX) {
            x = x - originX < 0 ? originX - reachX : originX + reachX;
        }
    }

    private void moveUp() {
        vy = -v;
    }
    private void moveDown() {
        vy = v;
    }

    // Horizontal velocity is based on how far the player is from origin
    private void moveLeft() {
        vx = -v * (float) Math.pow((4*reachX - Math.abs(x - originX))/(4*reachX), 2);
    }
    private void moveRight() {
        vx = v * (float) Math.pow( (4*reachX - Math.abs(x - originX))/(4*reachX), 2);
    }


    // Drawing

    public Circle draw() {
        Circle playerCircle = new Circle();
        playerCircle.setRadius(radius);
        playerCircle.setCenterX(x);
        playerCircle.setCenterY(y);
        playerCircle.setStyle("-fx-fill: red; -fx-border-color: black; -fx-border-width: 20px;");

        return playerCircle;
    }

}
