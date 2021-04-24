package airhockey.environment;

import javafx.scene.Node;

import java.util.*;

/*
* Rink can be interpreted as the games environment. It controls the flow and bounding box of the game.
* */

public class Rink {

    // Dimensions
    private int width;
    private int height;
    private final int MIN_WIDTH = 100;
    private final int MIN_HEIGHT = 100;

    // Items in rink (Delegates)
    Player playerLeft;
    Player playerRight;

    List<Puck> pucks = new ArrayList<>();
    RandomPuckSpawner randomPuckSpawner;

    Goal goalLeft;
    Goal goalRight;

    TwoPlayerScoreBoard scoreBoard;
    CountDown countDown;

    // Logic
    private final int PUCK_SPAWN_RATE = 10; // n per minute
    private final int PUCK_SPAWN_TIME = 1; // s
    private final long tickInterval = 10; // ms per tick

    // Constructor for initializing game and loading files
    public Rink(int width, int height) {
        setWidth(width);
        setHeight(height);

        playerLeft = new Player(6f/100f * getWidth(), this, 4f/10f * getWidth(), 24/1000f * getWidth(), Side.LEFT, "Player 1");
        playerRight = new Player(width - 6f/100f * getWidth(), this, 4f/10f * getWidth(), 24/1000f * getWidth(), Side.RIGHT, "Player 2");

        goalLeft = new Goal(Side.LEFT, 8f/30f * getHeight(), this);
        goalRight = new Goal(Side.RIGHT, 8f/30f * getHeight(), this);

        scoreBoard = new TwoPlayerScoreBoard();
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
                scoreBoard.addScore(Side.RIGHT, 1);

                // Remove puck unless it is the last one
                if (pucks.size() < 1) {
                    puckIterator.remove();
                } else {
                    puck.resetTo(playerLeft);
                }
            }
            else
            if (goalRight.isGoal(puck)) {
                scoreBoard.addScore(Side.LEFT, 1);

                // Remove puck unless it is the last one
                if (pucks.size() > 1) {
                    puckIterator.remove();
                } else {
                    puck.resetTo(playerRight);
                }
            }

        }

        // Attempt to spawn more pucks unless it is already spawning one
        if (randomPuckSpawner != null) {
            randomPuckSpawner.tick();
            // Remove RandomPuckSpawner if it is finished
            if (randomPuckSpawner.isFinished())
                randomPuckSpawner = null;
        } else if (shouldNewPuckSpawn()) {
            spawnNewPuck();
        }

        // Update countdown
        countDown.tick();

        return isGameFinished();

    }

    // Getters and setters
    public int getWidth() {
        return width;
    }

    private void setWidth(int width) {
        if (width < MIN_WIDTH)
            throw new IllegalArgumentException("Rink width can't be smaller than %s: %s".formatted(MIN_WIDTH, width));
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    private void setHeight(int height) {
        if (height < MIN_HEIGHT)
            throw new IllegalArgumentException("Rink height can't be smaller than %s: %s".formatted(MIN_HEIGHT, height));
        this.height = height;
    }

    public long getTickInterval() {
        return tickInterval;
    }

    public int getTimeInSeconds() {
        return countDown.getTimeInWholeSeconds();
    }

    public int getScoreOf(Side side) {
        return scoreBoard.getScoreOf(side);
    }

    public String getWinnerText() {
        Side winnerSide = scoreBoard.getWinner();
        if (winnerSide == null) {
            return "Tie!";
        } else {
            return getPlayerNameOf(winnerSide) + " won!";
        }
    }

    public String getPlayerNameOf(Side side) {
        if (side == null) {
            throw new IllegalArgumentException("Side cannot be null");
        }
        return switch (side) {
            case LEFT -> playerLeft.getName();
            case RIGHT -> playerRight.getName();
        };
    }

    void setPlayer(Side side, Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }

        switch (side) {
            case LEFT -> this.playerLeft = player;
            case RIGHT -> this.playerRight = player;
        }
    }

    // Player movement

    public void setPlayerPressing(Side side, Direction dir, boolean active) {
        if (side == null)
            throw new IllegalArgumentException("side cannot be null");
        switch (side) {
            case LEFT -> playerLeft.setPlayerPressing(dir, active);
            case RIGHT -> playerRight.setPlayerPressing(dir, active);
        }
    }

    // Puck spawning logic

    private boolean shouldNewPuckSpawn() {
        return Math.random() < getTickInterval()/1000f * PUCK_SPAWN_RATE /60f;
    }

    public void spawnNewPuck() {
        randomPuckSpawner = new RandomPuckSpawner(this, PUCK_SPAWN_TIME);
    }

    void clearPucks() {
        pucks.clear();
    }

    public Node[] generateSnapshotOfGame() {
        Node[] drawings = new Node[4 + pucks.size() + (randomPuckSpawner != null ? 1 : 0)];

        drawings[0] = playerLeft.draw();
        drawings[1] = playerRight.draw();
        drawings[2] = goalLeft.draw();
        drawings[3] = goalRight.draw();

        for (int i=0; i<pucks.size(); i++) {
            drawings[4+i] = pucks.get(i).draw();
        }

        if (randomPuckSpawner != null)
            drawings[drawings.length-1] = randomPuckSpawner.draw();

        return drawings;
    }

    public boolean isGameFinished() {
        return countDown.isFinished();
    }
}
