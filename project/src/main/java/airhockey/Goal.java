package airhockey;

import javafx.scene.shape.Rectangle;

public class Goal {

    private float centerY;
    private float size;
    private float width = 20;
    private GoalSide side;

    private Rink rink;

    public Goal(GoalSide side, float size, float centerY, Rink rink) {
        this.rink = rink;
        this.side = side;
        setSize(size);
        this.centerY = centerY;
    }

    public Goal(GoalSide side, float size, Rink rink) {
        this(side, size, (float) rink.getHeight()/2, rink);
    }

    public void setSize(float size) {
        if (size > rink.getHeight()) {
            throw new IllegalArgumentException(String.format("Goal cannot be larger than rink: (size: %s, goal: %s)", rink.getHeight(), size));
        }
        this.size = size;
    }

    public boolean isGoal (Puck puck) {
        // Check if puck is in same height as goal
        if ( !(centerY - size/2 <= puck.getY() && puck.getY() <= centerY + size/2) )
            return false;
        // Return if puck is in goal
        return switch (side) {
            case LEFT -> puck.getX() - puck.getRadius() <= 0;
            case RIGHT -> puck.getX() + puck.getRadius() >= rink.getWidth();
        };
    }

    // Drawing

    public Rectangle draw() {
        Rectangle goalRectangle = new Rectangle();
        goalRectangle.setWidth(width);
        goalRectangle.setHeight(size);
        switch (side) {
            case LEFT -> goalRectangle.setLayoutX(-width/2);
            case RIGHT -> goalRectangle.setLayoutX(rink.getWidth()-width/2);
        }
        goalRectangle.setLayoutY(centerY-size/2);

        return goalRectangle;
    }
}
