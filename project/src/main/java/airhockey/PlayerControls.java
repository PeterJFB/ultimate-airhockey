package airhockey;

public class PlayerControls {
    /*
    * Convert discrete events to continuous values which Player-class can rely on.
    * */

    boolean pressingUp = false;
    boolean pressingDown = false;
    boolean pressingLeft = false;
    boolean pressingRight = false;

    public PlayerControls() {

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
}
