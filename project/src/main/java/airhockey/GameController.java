package airhockey;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class GameController {

    Timer timer = new Timer();

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            drawGame();
        }
    };

    @FXML
    private AnchorPane root;
    @FXML
    private Pane rinkPane;
    @FXML
    private Text scoreText;
    @FXML
    private Text playerLeftNameText;
    @FXML
    private Text playerRightNameText;
    @FXML
    private Text countDownText;

    private Rink rink;

    @FXML
    public void initialize() {
        rink = new Rink(500, 300);

        rinkPane.setMaxWidth(rink.getWidth());
        rinkPane.setMaxHeight(rink.getHeight());
        rinkPane.setMinWidth(rink.getWidth());
        rinkPane.setMinHeight(rink.getHeight());

        drawGame();
    }

    public void drawGame() {


        rinkPane.getChildren().clear();

        rinkPane.getChildren().add(rink.playerLeft.draw());
        rinkPane.getChildren().add(rink.playerRight.draw());
        rinkPane.getChildren().add(rink.puck.draw());
        rinkPane.getChildren().add(rink.goalLeft.draw());
        rinkPane.getChildren().add(rink.goalRight.draw());

        Map<String, Integer> currentScore = rink.scoreBoard.getScore();
        scoreText.setText(currentScore.get(rink.playerLeft.getName()) + " - " + currentScore.get(rink.playerRight.getName()));

        countDownText.setText("%02d".formatted(rink.countDown.getTimeInSeconds()));

    }

    // Set player input
    public void handleKeyPress(KeyEvent keyEvent) {

        // Player 1
        if (keyEvent.getCode() == KeyCode.W) {
            rink.playerLeft.setPressingUp(true);
            rink.playerLeft.setPressingDown(false);
        }
        if (keyEvent.getCode() == KeyCode.S) {
            rink.playerLeft.setPressingUp(false);
            rink.playerLeft.setPressingDown(true);
        }
        if (keyEvent.getCode() == KeyCode.A) {
            rink.playerLeft.setPressingLeft(true);
            rink.playerLeft.setPressingRight(false);
        }
        if (keyEvent.getCode() == KeyCode.D) {
            rink.playerLeft.setPressingLeft(false);
            rink.playerLeft.setPressingRight(true);
        }

        // Player 2
        if (keyEvent.getCode() == KeyCode.I) {
            rink.playerRight.setPressingUp(true);
            rink.playerRight.setPressingDown(false);
        }
        if (keyEvent.getCode() == KeyCode.K) {
            rink.playerRight.setPressingUp(false);
            rink.playerRight.setPressingDown(true);
        }
        if (keyEvent.getCode() == KeyCode.J) {
            rink.playerRight.setPressingLeft(true);
            rink.playerRight.setPressingRight(false);
        }
        if (keyEvent.getCode() == KeyCode.L) {
            rink.playerRight.setPressingLeft(false);
            rink.playerRight.setPressingRight(true);
        }

        // Other game controllers

        // Run game TODO: make into own method?
        if (keyEvent.getCode() == KeyCode.R) {
            if (task != null)
                task.cancel();
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        // Logic
                        rink.tick();
                        // Drawing
                        drawGame();

                    });
                }
            };
            timer.scheduleAtFixedRate(task, 0, rink.getTickInterval());
        }

        // Stop game
        if (keyEvent.getCode() == KeyCode.X)
            task.cancel();
    }

    public void handleKeyRelease(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.W) {
            rink.playerLeft.setPressingUp(false);
        }
        if (keyEvent.getCode() == KeyCode.S) {
            rink.playerLeft.setPressingDown(false);
        }
        if (keyEvent.getCode() == KeyCode.A) {
            rink.playerLeft.setPressingLeft(false);
        }
        if (keyEvent.getCode() == KeyCode.D) {
            rink.playerLeft.setPressingRight(false);
        }
        if (keyEvent.getCode() == KeyCode.I) {
            rink.playerRight.setPressingUp(false);
        }
        if (keyEvent.getCode() == KeyCode.K) {
            rink.playerRight.setPressingDown(false);
        }
        if (keyEvent.getCode() == KeyCode.J) {
            rink.playerRight.setPressingLeft(false);
        }
        if (keyEvent.getCode() == KeyCode.L) {
            rink.playerRight.setPressingRight(false);
        }
    }
}
