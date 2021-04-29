package airhockey.environment;

abstract class PlayerControls {
    /*
     * The class converts discrete events to continuous values which the Player-class can rely on.
     * No tests are written since other classes was deemed more critical.
     * */

    private boolean pressingUp = false;
    private boolean pressingDown = false;
    private boolean pressingLeft = false;
    private boolean pressingRight = false;


    boolean isPressingUp() {
        return pressingUp;
    }

    boolean isPressingDown() {
        return pressingDown;
    }

    boolean isPressingLeft() {
        return pressingLeft;
    }

    boolean isPressingRight() {
        return pressingRight;
    }

    public void setPressingUp(boolean pressingUp) {
        this.pressingUp = pressingUp;
    }

    public void setPressingDown(boolean pressingDown) {
        this.pressingDown = pressingDown;
    }

    public void setPressingLeft(boolean pressingLeft) {
        this.pressingLeft = pressingLeft;
    }

    public void setPressingRight(boolean pressingRight) {
        this.pressingRight = pressingRight;
    }

    public void setPlayerPressing(Direction dir, boolean active) {
        if (dir == null) {
            throw new IllegalArgumentException("dir cannot be null.");
        }
        switch (dir) {
            case UP -> setPressingUp(active);
            case DOWN -> setPressingDown(active);
            case LEFT -> setPressingLeft(active);
            case RIGHT -> setPressingRight(active);
        }
    }

}
