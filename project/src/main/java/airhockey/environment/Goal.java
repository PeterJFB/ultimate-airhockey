package airhockey.environment;

import javafx.scene.shape.Rectangle;

class Goal {

    // Dimensions
    private float size;
    private final float width = 20;
    private float centerY;

    // Other
    private final Side side;
    private final Rink rink;

    public Goal(Side side, float size, float centerY, Rink rink) {
        this.rink = rink;
        this.side = side;
        setSize(size);
        setCenterY(centerY);
    }

    // Getters and Setters

    public Goal(Side side, float size, Rink rink) {
        this(side, size, (float) rink.getHeight()/2, rink);
    }

    public float getSize() {
        return size;
    }

    public float getWidth() {
        return width;
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
        // Check if puck is in same height as goal
        if ( !(getCenterY() - getSize()/2 <= puck.getY() && puck.getY() <= getCenterY() + getSize()/2) )
            return false;
        // Return if puck is in goal
        return switch (getSide()) {
            case LEFT -> puck.getX() - puck.getRadius() <= 0;
            case RIGHT -> puck.getX() + puck.getRadius() >= rink.getWidth();
        };
    }

    // Drawing

    public Rectangle draw() {
        Rectangle goalRectangle = new Rectangle();
        goalRectangle.setWidth(getWidth());
        goalRectangle.setHeight(getSize());
        switch (getSide()) {
            case LEFT -> goalRectangle.setLayoutX(-getWidth()/2f);
            case RIGHT -> goalRectangle.setLayoutX(rink.getWidth()-getWidth()/2f);
        }
        goalRectangle.setLayoutY(getCenterY()-getSize()/2f);

        return goalRectangle;
    }
}
