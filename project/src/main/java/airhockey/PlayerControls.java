package airhockey;

public class PlayerControls {
    protected boolean pressingUp = false;
    protected boolean pressingDown = false;
    protected boolean pressingLeft = false;
    protected boolean pressingRight = false;

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
