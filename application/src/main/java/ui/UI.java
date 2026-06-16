package ui;

import domain.Invader;
import domain.Tower;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import domain.Game;

public class UI extends Application {

    private Canvas mapCanvas = new Canvas(644, 670);
    private GraphicsContext graphicsContext =
            mapCanvas.getGraphicsContext2D();

    private BorderPane borderPane =
            new BorderPane(mapCanvas);

    private Scene gameScene =
            new Scene(borderPane);

    private Game game;
    private int selectedTurretId;
    private boolean waveIsOn = false;

    // Images
    private Image tower1Image;
    private Image tower2Image;
    private Image invaderImage;

    @Override
    public void start(Stage window) {

        tower1Image = new Image(
                getClass()
                        .getResource("/Tower1Transparent.png")
                        .toExternalForm());

        tower2Image = new Image(
                getClass()
                        .getResource("/Tower2Transparent.png")
                        .toExternalForm());

        invaderImage = new Image(
                getClass()
                        .getResource("/InvaderTransparent.png")
                        .toExternalForm());

        System.out.println("Tower1 = "
                + getClass().getResource("/Tower1Transparent.png"));

        System.out.println("Tower2 = "
                + getClass().getResource("/Tower2Transparent.png"));

        System.out.println("Invader = "
                + getClass().getResource("/InvaderTransparent.png"));

        System.out.println("Map = "
                + getClass().getResource("/hello_world2.txt"));

        GridPane startMenu = new GridPane();
        Scene startScene = new Scene(startMenu);

        Label mapName = new Label("File name for map");
        startMenu.add(mapName, 0, 0);

        TextField mapNameTxt =
                new TextField("hello_world2.txt");
        startMenu.add(mapNameTxt, 1, 0);

        Label invaderHp =
                new Label("Invader hp %");
        startMenu.add(invaderHp, 0, 1);

        TextField invaderHpTxt =
                new TextField("100");
        startMenu.add(invaderHpTxt, 1, 1);

        Button startGameButton =
                new Button("Start game");

        startGameButton.setOnAction((event) -> {

            startGame(
                    window,
                    mapNameTxt.getText(),
                    Integer.parseInt(invaderHpTxt.getText())
            );

        });

        startMenu.add(startGameButton, 1, 10);

        startMenu.setVgap(5);
        startMenu.setPadding(
                new Insets(10, 20, 20, 20));

        window.setTitle("Tower Defence");
        window.setScene(startScene);
        window.show();
    }

    /**
     * Actual game is in this method.
     */
    public void startGame(Stage window,
                          String mapFileName,
                          Integer hpPct) {

        Button buildTower1 = new Button("Build turret id 0");
        buildTower1.setOnAction(event -> {
            this.selectedTurretId = 0;
        });

        Button buildTower2 = new Button("Build turret id 1");
        buildTower2.setOnAction(event -> {
            this.selectedTurretId = 1;
        });

        Button startNextWave = new Button("Start next wave");
        startNextWave.setOnAction(event -> {
            if (!waveIsOn) {
                game.nextWave();
                waveIsOn = true;
            }
        });

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 20, 20, 20));

        hbox.getChildren().add(buildTower1);
        hbox.getChildren().add(buildTower2);
        hbox.getChildren().add(startNextWave);

        borderPane.setBottom(hbox);

        game = new Game(mapFileName, hpPct);

        window.setScene(gameScene);

        new AnimationTimer() {

            long previous = 0;

            @Override
            public void handle(long now) {

                if (now - previous < 1000000000 / 10) {
                    return;
                }

                previous = now;

                gameScene.setOnMouseClicked(event -> {
                    double xLocation = event.getX();
                    double yLocation = event.getY();

                    game.buildTower(
                            selectedTurretId,
                            xLocation,
                            yLocation);
                });

                if (waveIsOn) {
                    game.moveAllInvaders();
                    game.attackWithAllTowers();
                }

                drawMap(window);

                if (game.getInvadersAlive().isEmpty() && waveIsOn) {
                    waveIsOn = false;
                }
            }
        }.start();
    }

    /**
     * Map, towers and invaders are drawn here.
     */
    public void drawMap(Stage window) {

    graphicsContext.clearRect(0, 0, 700, 700);

    graphicsContext.drawImage(tower1Image, 50, 50);

    // rest of your drawing code...
}

        // TEST IMAGE
        // Remove this later if desired
        graphicsContext.drawImage(tower1Image, 50, 50);

        // Draw towers
        for (Tower tower : game.getTowers()) {

            if (tower.getId() == 0) {
                graphicsContext.drawImage(
                        tower1Image,
                        tower.getPixelX(),
                        tower.getPixelY() - 40);

            } else if (tower.getId() == 1) {
                graphicsContext.drawImage(
                        tower2Image,
                        tower.getPixelX(),
                        tower.getPixelY() - 45);
            }
        }

        // Draw invaders
        for (Invader invader : game.getInvadersAlive()) {
            graphicsContext.drawImage(
                    invaderImage,
                    invader.getPixelX(),
                    invader.getPixelY());
        }
    }

    /**
     * Main method.
     */
    public static void main(String[] args) {
        launch(UI.class);
    }
}