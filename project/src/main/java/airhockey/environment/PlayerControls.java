package airhockey.environment;

abstract class PlayerControls {
    /*
     * Convert discrete events to continuous values which Player-class can rely on.
     * */

    boolean pressingUp = false;
    boolean pressingDown = false;
    boolean pressingLeft = false;
    boolean pressingRight = false;

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
        switch (dir) {
            case UP -> setPressingUp(active);
            case DOWN -> setPressingDown(active);
            case LEFT -> setPressingLeft(active);
            case RIGHT -> setPressingRight(active);
        }
    }
}
