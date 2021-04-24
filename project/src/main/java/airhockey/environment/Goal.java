package airhockey.environment;

import javafx.scene.shape.Rectangle;

class Goal {

    // Dimensions
    private float size;
    private final float WIDTH = 20;
    private float centerY;

    // Other
    private final Side side;
    private final Rink rink;
    private Rectangle goalRectangle;

    public Goal(Side side, float size, float centerY, Rink rink) {
        if (rink == null) {
            throw new IllegalArgumentException("rink cannot be null.");
        }
        this.rink = rink;
        if (side == null) {
            throw new IllegalArgumentException("side cannot be null.");
        }
        this.side = side;
        setSize(size);
        setCenterY(centerY);
        createGoalRectangle();
    }

    // Getters and Setters

    public Goal(Side side, float size, Rink rink) {
        this(side, size, (float) rink.getHeight()/2, rink);
    }

    public float getSize() {
        return size;
    }

    public float getWidth() {
        return WIDTH;
    }

    public Side getSide() {
        return side;
    }

    public void setSize(float size) {
        if (size > rink.getHeight()) {
            throw new IllegalArgumentException(String.format("Goal cannot be larger than rink: (size: %s, goal: %s)", rink.getHeight(), size));
        }
        this.size = size;
    }

    private float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        if (centerY - size/2f < 0 || size/2f + centerY > rink.getHeight())
            throw new IllegalArgumentException("Goal is not within bounds of rink: (center: " + centerY + ", size: " + size + ")");
        this.centerY = centerY;
    }

    public boolean isGoal (Puck puck) {
        if (puck == null)
            return false;
        // Check if puck is at same height as goal
        if ( !(getCenterY() - getSize()/2 <= puck.getY() && puck.getY() <= getCenterY() + getSize()/2) )
            return false;
        // Return if puck is in goal
        return switch (getSide()) {
            case LEFT -> puck.getX() - puck.getRadius() <= 0;
            case RIGHT -> puck.getX() + puck.getRadius() >= rink.getWidth();
        };
    }

    // Drawing

    public void createGoalRectangle() {
        goalRectangle = new Rectangle();
        goalRectangle.setWidth(getWidth());
        goalRectangle.setHeight(getSize());
        switch (getSide()) {
            case LEFT -> goalRectangle.setLayoutX(-getWidth()/2f);
            case RIGHT -> goalRectangle.setLayoutX(rink.getWidth()-getWidth()/2f);
        }
        goalRectangle.setLayoutY(getCenterY()-getSize()/2f);
        goalRectangle.cacheProperty().set(true);
    }

    public Rectangle draw() {
        return goalRectangle;
    }
}
