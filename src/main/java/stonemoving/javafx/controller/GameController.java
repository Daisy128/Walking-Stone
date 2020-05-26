package stonemoving.javafx.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import stonemoving.results.GameResult;
import stonemoving.results.GameResultDao;
import stonemoving.state.StoneState;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Control the processing page of the game.
 */
@Slf4j
public class GameController {

    @Inject
    private FXMLLoader fxmlLoader;

    @Inject
    private GameResultDao gameResultDao;

    private String playerName;
    private StoneState gameState;
    private IntegerProperty steps = new SimpleIntegerProperty();
    private IntegerProperty scores = new SimpleIntegerProperty();
    private Instant startTime;
    private List<Image> stoneImages;

    @FXML
    private Label messageLabel;

    @FXML
    private GridPane gameGrid;

    @FXML
    private Label stepsLabel;

    @FXML
    private Label scoresLabel;

    @FXML
    private Label stopWatchLabel;

    private Timeline stopWatchTimeline;

    @FXML
    private Button resetButton;

    @FXML
    private Button giveUpButton;

    private BooleanProperty gameOver = new SimpleBooleanProperty();

    /**
     * Set the player's name when he wants to access in the game.
     * @param playerName the name setting by the player.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Initialize the game page with all images and the stone location.
     */
    @FXML
    public void initialize() {
        stoneImages = List.of(
                new Image(getClass().getResource("/images/stone.png").toExternalForm()),
                new Image(getClass().getResource("/images/unframed.png").toExternalForm()),
                new Image(getClass().getResource("/images/tranframed.png").toExternalForm())
        );
        scoresLabel.textProperty().bind(scores.asString());
        stepsLabel.textProperty().bind(steps.asString());
        gameOver.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                log.info("Game is over");
                log.debug("Saving result to database...");
                gameResultDao.persist(createGameResult());
                stopWatchTimeline.stop();
            }
        });
        resetGame();
    }

    private void resetGame() {
        gameState = new StoneState(StoneState.INITIAL);
        steps.set(0);
        scores.set(0);
        startTime = Instant.now();
        gameOver.setValue(false);
        displayGameState();
        createStopWatch();
        Platform.runLater(() -> messageLabel.setText("Good luck, " + playerName + "!"));
    }

    private void displayGameState() {
        ImageView view;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                view = (ImageView) gameGrid.getChildren().get(i * 8 + j);
                log.trace("Image({}, {}) = {}", i, j, view.getImage());

                if (gameState.getMatrix()[i][j].getValue() == 0) {
                    view.setImage(stoneImages.get(0));
                }
                else if(gameState.getMatrix()[i][j].getValue() == 1) {
                    view.setImage(stoneImages.get(1));
                }
                else{
                    view.setImage(stoneImages.get(2));
                }
            }
        }
    }

    /**
     * The stone is moved when the player clicks the next step.
     * @param mouseEvent is when the player clicks on pane.
     */
    public void handleClickOnStone(MouseEvent mouseEvent) {
        int row = GridPane.getColumnIndex((Node) mouseEvent.getSource());
        int col = GridPane.getRowIndex((Node) mouseEvent.getSource());
        String mark = ((Label) mouseEvent.getSource()).getText();
        log.debug("Path ({}, {}) is chosen", row, col);
        if (! gameState.isSolved() && gameState.canBeMoved(row, col)) {
            steps.set(steps.get() + 1);
            if(row!=7 || col!=7)
                scores.set(scores.get() + Integer.parseInt(mark));
            gameState.moveToNext(row, col);
            if (gameState.isSolved()) {
                gameOver.setValue(true);
                log.info("Player {} has solved the game in {} steps", playerName, steps.get());
                messageLabel.setText("Congratulations, " + playerName + "!");
                resetButton.setDisable(true);
                giveUpButton.setText("Finish");
            }
        }
        displayGameState();
    }

    /**
     * Reset the game to initial state.
     * @param actionEvent is the action that the player clicks on that button.
     */
    public void handleResetButton(ActionEvent actionEvent)  {
        log.debug("{} is pressed", ((Button) actionEvent.getSource()).getText());
        log.info("Resetting game...");
        stopWatchTimeline.stop();
        resetGame();
    }

    /**
     * Give up the game and go to the final high score list.
     * @param actionEvent is when the player clicks on "give up".
     * @throws IOException is thrown when the action fails.
     */
    public void handleGiveUpButton(ActionEvent actionEvent) throws IOException {
        String buttonText = ((Button) actionEvent.getSource()).getText();
        log.debug("{} is pressed", buttonText);
        if (buttonText.equals("Give Up")) {
            log.info("The game has been given up");
        }
        gameOver.setValue(true);
        log.info("Loading high scores scene...");
        fxmlLoader.setLocation(getClass().getResource("/fxml/highscores.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private GameResult createGameResult() {
        GameResult result = GameResult.builder()
                .player(playerName)
                .solved(gameState.isSolved())
                .duration(Duration.between(startTime, Instant.now()))
                .steps(scores.get()/steps.get())
                .build();
        return result;
    }

    private void createStopWatch() {
        stopWatchTimeline = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e -> {
            long millisElapsed = startTime.until(Instant.now(), ChronoUnit.MILLIS);
            stopWatchLabel.setText(DurationFormatUtils.formatDuration(millisElapsed, "HH:mm:ss"));
        }), new KeyFrame(javafx.util.Duration.seconds(1)));
        stopWatchTimeline.setCycleCount(Animation.INDEFINITE);
        stopWatchTimeline.play();
    }
}
