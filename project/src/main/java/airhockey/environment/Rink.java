package airhockey.environment;

import javafx.scene.Node;

import java.util.*;

// Potentially change name to environment
public class Rink {

    // Dimensions
    private int width;
    private int height;
    private final int minWidth = 100;
    private final int minHeight = 100;

    // Items in rink (Delegates)
    public Player playerLeft;
    public Player playerRight;

    public List<Puck> pucks = new ArrayList<>();
    public PuckSpawner puckSpawner;

    public Goal goalLeft;
    public Goal goalRight;

    public TwoPlayerScoreBoard scoreBoard;
    public CountDown countDown;

    // Logic
    private final int puckSpawnRate = 10; // n per minute
    private final long tickInterval = 10; // ms per tick

    // Constructor for initializing game and loading files
    public Rink(int width, int height) {
        setWidth(width);
        setHeight(height);

        playerLeft = new Player(6f/100f * getWidth(), this, 4f/10f * getWidth(), 24/1000f * getWidth(), Side.LEFT, "Player 1");
        playerRight = new Player(width - 6f/100f * getWidth(), this, 4f/10f * getWidth(), 24/1000f * getWidth(), Side.RIGHT, "Player 2");

        goalLeft = new Goal(Side.LEFT, 8f/30f * getHeight(), this);
        goalRight = new Goal(Side.RIGHT, 8f/30f * getHeight(), this);

        scoreBoard = new TwoPlayerScoreBoard(playerLeft.getName(), playerRight.getName());
        countDown = new CountDown(120, tickInterval / 1000f);

        pucks.add(new Puck(18, this));
    }

    // Main
    public boolean tick() {

        // Move Players
        playerLeft.updateMovement();
        playerRight.updateMovement();

        // Check collision with other pucks
        for (int i=0; i<pucks.size(); i++) {
            for (int j=i+1; j<pucks.size(); j++) {
                if (pucks.get(i).isCollidingWith(pucks.get(j))) {
                    pucks.get(i).performCollisionWith(pucks.get(j));
                }
            }
        }

        // Check pucks status
        Iterator<Puck> puckIterator = pucks.iterator();
        while (puckIterator.hasNext()) {
            Puck puck = puckIterator.next();

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
                puck.resetTo(playerLeft);

                if (pucks.size() > 1) {
                    puckIterator.remove();
                }
            }
            else
            if (goalRight.isGoal(puck)) {
                scoreBoard.addScore(playerLeft.getName(), 1);
                puck.resetTo(playerRight);

                if (pucks.size() > 1) {
                    puckIterator.remove();
                }
            }

        }

        // Spawn more pucks
        if (puckSpawner != null) {
            puckSpawner.tick();
            if (puckSpawner.isFinished())
                puckSpawner = null;
        } else if (shouldNewPuckSpawn()) {
            spawnNewPuck();
        }

        // Update countdown
        countDown.tick();

        return (countDown.isFinished());

    }

    // Getters and setters
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

    public String getWinnerText() {
        String winner = scoreBoard.getWinner();
        if (winner.isBlank()) {
            return "Tie!";
        } else {
            return winner + " won!";
        }
    }

    void setPlayerLeft(Player playerLeft) {
        if (playerLeft == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        this.playerLeft = playerLeft;
    }

    void setPlayerRight(Player playerRight) {
        if (playerRight == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        this.playerRight = playerRight;
    }

    // Player movement

    public void setPlayerPressing(Side side, Direction dir, boolean active) {
        switch (side) {
            case LEFT -> playerLeft.setPlayerPressing(dir, active);
            case RIGHT -> playerRight.setPlayerPressing(dir, active);
        }
    }

    // Puck spawning logic

    public boolean shouldNewPuckSpawn() {
        return Math.random() < getTickInterval()/1000f * puckSpawnRate/60f;
    }

    public void spawnNewPuck() {
        puckSpawner = new PuckSpawner(this, 1);
    }

    public void clearPucks() {
        pucks.clear();
    }

    public Node[] generateSnapshot() {
        Node[] drawings = new Node[4 + pucks.size() + (puckSpawner != null ? 1 : 0)];

        drawings[0] = playerLeft.draw();
        drawings[1] = playerRight.draw();
        drawings[2] = goalLeft.draw();
        drawings[3] = goalRight.draw();

        for (int i=0; i<pucks.size(); i++) {
            drawings[4+i] = pucks.get(i).draw();
        }

        if (puckSpawner != null)
            drawings[drawings.length-1] = puckSpawner.draw();

        return drawings;
    }
}