package airhockey;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lib.SaveController;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class GameController {

    Timer timer = new Timer(true);
    TimerTask task = null;

    final SaveController<Rink> saveHandler = new airhockey.SaveHandler();
    FileChooser fileChooser = new FileChooser();

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
    private Text winnerText;
    @FXML
    private Text countDownText;
    @FXML
    private Button startAndPauseButton;

    private Rink rink;

    @FXML
    public void initialize() {
        rink = new Rink(1000, 600);

        rinkPane.setPrefWidth(rink.getWidth());
        rinkPane.setPrefHeight(rink.getHeight());
        playerLeftNameText.setText(rink.playerLeft.getName());
        playerRightNameText.setText(rink.playerRight.getName());

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));

        drawGame();
    }

    public void drawGame() {
        rinkPane.getChildren().clear();

        rinkPane.getChildren().add(rink.playerLeft.draw());
        rinkPane.getChildren().add(rink.playerRight.draw());
        for (Puck puck : rink.pucks) {
            rinkPane.getChildren().add(puck.draw());
        }
        rinkPane.getChildren().add(rink.goalLeft.draw());
        rinkPane.getChildren().add(rink.goalRight.draw());
        if (rink.puckSpawner != null)
            rinkPane.getChildren().add(rink.puckSpawner.draw());

        Map<String, Integer> currentScore = rink.scoreBoard.getScore();
        scoreText.setText(currentScore.get(rink.playerLeft.getName()) + " - " + currentScore.get(rink.playerRight.getName()));

        countDownText.setText("%02d".formatted(rink.countDown.getTimeInSeconds()));

    }

    // Handle player input

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
        if (keyEvent.getCode() == KeyCode.ENTER) {
            playGame();
        }
        if (keyEvent.getCode() == KeyCode.P) {
            rink.spawnNewPuck();
        }

    }

    public void handleKeyRelease(KeyEvent keyEvent) {
        // Player 1
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

        // Player 2
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

    // Change state of game

    public void playGame() {
        // Create clock to run game
        if (task != null)
            task.cancel();
        task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // Logic
                    boolean isOver = rink.tick();
                    // Drawing
                    drawGame();
                    if (isOver) {
                        winnerText.setText(rink.getWinnerText());
                        stopGame();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(task, 0, rink.getTickInterval());


        // Update state of buttons
        startAndPauseButton.setOnAction(actionEvent -> pauseGame());
        startAndPauseButton.setText("Pause");
    }

    public void pauseGame() {
        // stop task
        task.cancel();
        // Update state of buttons
        startAndPauseButton.setOnAction(actionEvent -> playGame());
        startAndPauseButton.setText("Resume");
    }

    public void stopGame() {
        // Happens only when game is over
        task.cancel();
        // Update state of buttons
        startAndPauseButton.setVisible(false);
    }

    public void restartGame() {
        // Stop game from running
        pauseGame();
        // Reset properties
        rink = new Rink(1000, 600);
        // Update state of buttons
        winnerText.setText("");
        startAndPauseButton.setText("Start");
        startAndPauseButton.setVisible(true);

        // Show changes
        drawGame();
    }

    // File interaction

    public void saveGame() throws Exception {
        pauseGame();
        try {

            // https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
            fileChooser.setTitle("Save game");
            // https://edencoding.com/stage-controller/
            fileChooser.setInitialFileName("save.json");
            File file = fileChooser.showSaveDialog(startAndPauseButton.getScene().getWindow()); // Nasty workaround
            if (file != null)
                saveHandler.save(file.getAbsolutePath(), rink);

        } catch (Exception e) {
            System.err.println("Attempted to write to file but an error occurred.");
            throw e;
        }
    }

    public void loadGame() throws Exception {
        pauseGame();
        try {
            // https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
            fileChooser.setTitle("Open saved game");
            // https://edencoding.com/stage-controller/
            File file = fileChooser.showOpenDialog(startAndPauseButton.getScene().getWindow());
            if (file != null) {
                rink = saveHandler.load(file.getAbsolutePath());
            }
            drawGame();
        } catch (Exception e) {
            System.err.println("Attempted to load from file but an error occurred.");
            throw e;
        }
    }
}
