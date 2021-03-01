package airhockey;

// Potentially change name to environment
public class Rink {
    private int width;
    private int height;
    final public int minWidth = 40;
    final public int minHeight = 20;

    public Player playerLeft;
    public Player playerRight;

    public Puck puck;

    public Goal goalLeft;
    public Goal goalRight;

    public TwoPlayerScoreBoard scoreBoard;
    public CountDown countDown;

    private long tickInterval = 10;

    public Rink(int width, int height) {
        setWidth(width);
        setHeight(height);

        playerLeft = new Player(30, this, 200, GoalSide.LEFT, "Jens");
        playerRight = new Player(width-30, this, 200, GoalSide.RIGHT, "Svein");

        goalLeft = new Goal(GoalSide.LEFT, 80, this);
        goalRight = new Goal(GoalSide.RIGHT, 80, this);

        scoreBoard = new TwoPlayerScoreBoard(playerLeft.getName(), playerRight.getName());
        countDown = new CountDown(60,  tickInterval/1000f);

        puck = new Puck(110, height/2f, 5, 5, this, 8);
    }


    public void tick() {

        // Move Players
        playerLeft.updateMovement();
        playerRight.updateMovement();

        // Check player collision
        if (puck.isCollidingWith(playerLeft))
            puck.performCollisionWith(playerLeft);

        if (puck.isCollidingWith(playerRight))
            puck.performCollisionWith(playerRight);


        // Check wall collision
        puck.updateWallCollision();

        // Update movement
        puck.moveForward();

        // Update game status
        if (goalLeft.isGoal(puck)) {
            scoreBoard.addScore(playerRight.getName(), 1);
            puck.resetTo(GoalSide.LEFT);
        }
        if (goalRight.isGoal(puck)) {
            scoreBoard.addScore(playerLeft.getName(), 1);
            puck.resetTo(GoalSide.RIGHT);
        }

        // Update countdown
        countDown.tick();

    }

    public int getWidth() {
        return width;
    }

    private void setWidth(int width) {
        if (width < minWidth)
            throw new IllegalArgumentException("Rink width can't be smaller than " + minWidth + ": " + width);
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    private void setHeight(int height) {
        if (height < minHeight)
            throw new IllegalArgumentException("Rink height can't be smaller than " + minHeight + ": " + height);
        this.height = height;
    }

    public long getTickInterval() {
        return tickInterval;
    }
}
