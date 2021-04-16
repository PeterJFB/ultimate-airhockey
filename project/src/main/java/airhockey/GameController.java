package airhockey;

import airhockey.environment.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import airhockey.lib.SaveController;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class GameController {

    Timer timer = new Timer(true);
    TimerTask task = null;

    final SaveController<Rink> saveHandler = new SaveHandler();
    final FileChooser fileChooser = new FileChooser();

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
        playerLeftNameText.setText("Player 1");
        playerRightNameText.setText("Player 2");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json"));

        drawGame();
    }


    // TODO: Ideally should every association rink be default aka not available to GameController
    public void drawGame() {
        /*
        * The game uses no canvas, even though it would most likely be more optimized as game graphics. This was
        * intentionally avoided since I already have experience with its equivalent library in javascript, and I wanted
        * to explore the different elements in javaFX. It also reduces numbers of lines in java (which i think i have
        * enough of already:) ), since i can use CSS to style them instead.
        * */

        rinkPane.getChildren().clear();

        for (Node e : rink.generateSnapshot()) {
            rinkPane.getChildren().add(e);
        }

//        rinkPane.getChildren().add(rink.playerLeft.draw());
//        rinkPane.getChildren().add(rink.playerRight.draw());
//        for (Puck puck : rink.pucks) {
//            rinkPane.getChildren().add(puck.draw());
//        }
//        rinkPane.getChildren().add(rink.goalLeft.draw());
//        rinkPane.getChildren().add(rink.goalRight.draw());
//        if (rink.puckSpawner != null)
//            rinkPane.getChildren().add(rink.puckSpawner.draw());

        Map<String, Integer> currentScore = rink.scoreBoard.getScore();
        scoreText.setText(currentScore.get(rink.playerLeft.getName()) + " - " + currentScore.get(rink.playerRight.getName()));

        countDownText.setText("%02d".formatted(rink.countDown.getTimeInSeconds()));

    }

    // Handle player input

    public void handleKeyPress(KeyEvent keyEvent) {

        switch (keyEvent.getCode()) {
            // Player 1
            case W -> {
                rink.setPlayerPressing(Side.LEFT, Direction.UP, true);
                rink.setPlayerPressing(Side.LEFT, Direction.DOWN, false);
            }
            case S -> {
                rink.setPlayerPressing(Side.LEFT, Direction.UP, false);
                rink.setPlayerPressing(Side.LEFT, Direction.DOWN, true);
            }
            case A -> {
                rink.setPlayerPressing(Side.LEFT, Direction.LEFT, true);
                rink.setPlayerPressing(Side.LEFT, Direction.RIGHT, false);
            }
            case D -> {
                rink.setPlayerPressing(Side.LEFT, Direction.LEFT, false);
                rink.setPlayerPressing(Side.LEFT, Direction.RIGHT, true);
            }

            // Player 2
            case I -> {
                rink.setPlayerPressing(Side.RIGHT, Direction.UP, true);
                rink.setPlayerPressing(Side.RIGHT, Direction.DOWN, false);
            }
            case K -> {
                rink.setPlayerPressing(Side.RIGHT, Direction.UP, false);
                rink.setPlayerPressing(Side.RIGHT, Direction.DOWN, true);

            }
            case J -> {
                rink.setPlayerPressing(Side.RIGHT, Direction.LEFT, true);
                rink.setPlayerPressing(Side.RIGHT, Direction.RIGHT, false);
            }
            case L -> {
                rink.setPlayerPressing(Side.RIGHT, Direction.LEFT, false);
                rink.setPlayerPressing(Side.RIGHT, Direction.RIGHT, true);
            }
            // Other
            case ENTER -> playGame();
            case P -> rink.spawnNewPuck();
        }

    }

    public void handleKeyRelease(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            // Player 1
            case W -> rink.setPlayerPressing(Side.LEFT, Direction.UP, false);
            case S -> rink.setPlayerPressing(Side.LEFT, Direction.DOWN, false);
            case A -> rink.setPlayerPressing(Side.LEFT, Direction.LEFT, false);
            case D -> rink.setPlayerPressing(Side.LEFT, Direction.RIGHT, false);

            // Player 2
            case I -> rink.setPlayerPressing(Side.RIGHT, Direction.UP, false);
            case K -> rink.setPlayerPressing(Side.RIGHT, Direction.DOWN, false);
            case J -> rink.setPlayerPressing(Side.RIGHT, Direction.LEFT, false);
            case L -> rink.setPlayerPressing(Side.RIGHT, Direction.RIGHT, false);
        }
    }

    // Change state of game

    public void playGame() {
        // Create clock to run game
        if (task != null)
            task.cancel();
        task = new TimerTask() { // TODO: Maybe make clock inside Rink and have GameController as observer.
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
        if (task != null)
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

        } catch (Exception e) { // Maybe move this inside save
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
