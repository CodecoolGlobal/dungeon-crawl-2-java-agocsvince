package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.items.Item;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.ArrayList;


public class GameEngine extends Application {
    public static ArrayList<AiActor> aiList = new ArrayList<AiActor>();
    GameDatabaseManager dbManager;


    public static SoundEngine soundEngine = new SoundEngine();
    GameMap map = MapLoader.loadMap(0);
    Player player = map.getPlayer();
    Canvas canvas = new Canvas(
            map.getWidth() * Tiles.TILE_WIDTH,
            map.getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Label inventoryLabel = new Label();
    Button pickupButton = new Button("Pick up item (E)");
    Button mute = new Button("Mute");

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        setupDbManager();
        GridPane ui = new GridPane();
        pickupButton.setFocusTraversable(false);
        pickupButton.setOnAction(actionEvent -> pickupButtonPressed());

        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));
        Label name = new Label("Player");
        mute.setFocusTraversable(false);
        mute.setOnAction(actionEvent -> toggleMute());
        Label inventory = new Label("Inventory: ");

        ui.add(name, 0, 0);
        ui.add(mute, 1, 0);
        ui.add(new Label("Health: "), 0, 1);
        ui.add(healthLabel, 1, 1);
        ui.add(pickupButton, 0, 2);
        ui.add(inventory, 0, 3);
        ui.add(inventoryLabel, 0, 4);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar(name));

        ui.setHgap(10);
        ui.setVgap(10);
        borderPane.setCenter(canvas);
        borderPane.setRight(ui);

        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        refresh();
        //This creates the event listener inside the scene for checking key input
        scene.setOnKeyPressed(this::onKeyPressed);
        primaryStage.setTitle("Dungeon Crawl");

        //This is the engines fixed time loop for calculating anything that doesn't correlate with the player actions
        KeyFrame enemyMovementFrame = new KeyFrame(Duration.millis(2000), e -> enemyMovement());
        Timeline timeline = new Timeline(enemyMovementFrame);
        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();

        //Keyboard shortcuts
        //Pick up with 'E'
        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (e.getCode().equals(KeyCode.E)) {
                pickupButtonPressed();
            } else if (e.getCode().equals(KeyCode.ADD)) {
                soundEngine.increaseVolume();
            } else if (e.getCode().equals(KeyCode.SUBTRACT)) {
                soundEngine.decreaseVolume();
            } else if (e.getCode().equals(KeyCode.M)) {
                soundEngine.skipTrack();
            }
        });
        soundEngine.start();

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void setupDbManager() {
        dbManager = new GameDatabaseManager();
        try {
            dbManager.setup();
        } catch (SQLException ex) {
            System.out.println("Cannot connect to database.");
        }
    }

    private void toggleMute() {
        soundEngine.toggleMute();
        if (soundEngine.isMuted()) {
            mute.setText("Unmute");
        } else {
            mute.setText("Mute");
        }
    }


    private MenuBar menuBar(Label name) {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("Settings");
        MenuItem add = new MenuItem("Change name");
        add.setOnAction(t -> {
            Stage stage = new Stage();
            TextField textField = new TextField();
            HBox hbox = new HBox(5);
            hbox.setPadding(new Insets(25));
            Label label1 = new Label("Name: ");
            Button button1 = new Button("Submit");
            button1.setOnAction(e -> setNameLabel(name, stage, textField));
            textField.setOnKeyPressed(k -> {
                if (k.getCode().equals(KeyCode.ENTER)) {
                    setNameLabel(name, stage, textField);
                }
            });
            hbox.setMinSize(100, 75);
            hbox.getChildren().addAll(label1, textField, button1);
            Scene scene = new Scene(hbox);
            stage.setScene(scene);
            stage.show();
        });
        menuFile.getItems().addAll(add);
        menuBar.getMenus().addAll(menuFile);
        return menuBar;
    }

    private void setNameLabel(Label name, Stage stage, TextField textField) {
        player.setName(textField.getText());
        name.setText(player.getName());
        stage.close();
    }

    private void enemyMovement() {
        for (AiActor ai : aiList) {
            if (!ai.isAlive()) {
                aiList.remove(ai);
                ai = null;
                return;
            }
            ai.makeMove();
        }
        refresh();
    }


    private void pickupButtonPressed() {
        Cell playerCell = player.getCell();
        Item item = playerCell.getItem();
        player.pickUpItem(item);
        playerCell.removeItemFromCell();
        pickupButton.setVisible(false);

        System.out.println(item.getName());
        inventoryLabel.setText(inventoryLabel.getText() + item.getName() + " ");
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case UP:
                map.getPlayer().move(0, -1);
                refresh();
                break;
            case DOWN:
                map.getPlayer().move(0, 1);
                refresh();
                break;
            case LEFT:
                map.getPlayer().move(-1, 0);
                refresh();
                break;
            case RIGHT:
                map.getPlayer().move(1, 0);
                refresh();
                break;
            case S:
                Player player = map.getPlayer();
                dbManager.savePlayer(player);
                break;
        }
    }

    private void refresh() {
        if (!player.isAlive())
            System.exit(0);
        context.setFill(Color.BLACK);
        context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        boolean setItemButton = false;

        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                Cell cell = map.getCell(x, y);
                if (cell.getActor() != null) {
                    Tiles.drawTile(context, cell.getActor(), x, y);
                    if (cell.hasItem() && cell.getActor() instanceof Player)
                        setItemButton = true;
                } else if (cell.getItem() != null) {
                    Tiles.drawTile(context, cell.getItem(), x, y);
                } else if (cell.getDoor() != null) {
                    Tiles.drawTile(context, cell.getDoor(), x, y);
                } else {
                    Tiles.drawTile(context, cell, x, y);
                }
            }
        }
        pickupButton.setVisible(false);
        if (setItemButton)
            pickupButton.setVisible(true);
        healthLabel.setText("" + map.getPlayer().getHealth());
    }

    private void onKeyReleased(KeyEvent keyEvent) {
        KeyCombination exitCombinationMac = new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
        KeyCombination exitCombinationWin = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
        if (exitCombinationMac.match(keyEvent)
                || exitCombinationWin.match(keyEvent)
                || keyEvent.getCode() == KeyCode.ESCAPE) {
            exit();
        }
    }

    private void exit() {
        try {
            stop();
        } catch (Exception e) {
            System.exit(1);
        }
        System.exit(0);
    }

}
