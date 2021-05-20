package com.codecool.dungeoncrawl;


import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import com.codecool.dungeoncrawl.logic.items.Item;


public class GameEngine extends Application {
    public static ArrayList<AiActor> aiList = new ArrayList<AiActor>();
    GameDatabaseManager dbManager;


    public static SoundEngine soundEngine = new SoundEngine();
    static GameMap map = MapLoader.loadMap(0);
    static Player player = map.getPlayer();
    Canvas canvas = new Canvas(
            map.getWidth() * Tiles.TILE_WIDTH,
            map.getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    private final Label healthLabel = new Label();
    private final Label inventoryLabel = new Label();
    private BorderPane borderPane = new BorderPane();
    private GridPane ui;
    private List<Label> menuLabels;
    private final Label name = new Label("Player");
    private final Button pickupButton = new Button("Pick up item (E)");
    private final Button mute = new Button("Mute");
    private static final Button[][] inventoryButtons = new Button[4][6];
    private List<Label> endLabels;
    private final TextField nameField = new TextField(player.getName());
    private static Button headButton;
    private static Button torsoButton;
    private static Button lHandButton;
    private static Button rHandButton;

    public static void main(String[] args) {
        launch(args);
    }

    public static Player getPlayer() {
        return player;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setupDbManager();

        Scene scene = new Scene(borderPane);
        setUpLabels(scene);

        torsoButton = makeCharacterButton();
        headButton = makeCharacterButton();
        lHandButton = makeCharacterButton();
        rHandButton = makeCharacterButton();

        setUpVBox(menuLabels);

        primaryStage.setScene(scene);
        refresh();
        primaryStage.setTitle("Dungeon Crawl");
        //This creates the event listener inside the scene for checking key input
        primaryStage.show();
    }

    private void setUpLabels(Scene scene) {
        Label menuLabelStart = newLabel("Start");
        Label menuLabelOptions = newLabel("Options");
        Label menuLabelExit = newLabel("Exit");
        menuLabels = Arrays.asList(menuLabelStart, menuLabelOptions, menuLabelExit);
        //Options
        Label optionsLabelName = newLabel("Name");
        Label optionsLabelBack = newLabel("Back");
        List<Label> optionLabels = Arrays.asList(optionsLabelName, optionsLabelBack);
        // End screen
        Label endScreenPlayAgain = newLabel("Play again");
        Label endScreenMenu = newLabel("Main menu");
        endLabels = Arrays.asList(newLabel("GAME OVER"), endScreenPlayAgain, endScreenMenu);
        //End screen actions
        endScreenPlayAgain.setOnMouseClicked(mouseEvent -> borderPane.setCenter(canvas));
        endScreenMenu.setOnMouseClicked(mouseEvent -> {
            setUpVBox(menuLabels);
        });
        //Menu actions
        menuLabelStart.setOnMouseClicked(mouseEvent -> {
            afterStart(scene);

        });
        menuLabelOptions.setOnMouseClicked(mouseEvent -> {
            //Option actions
            optionsLabelName.setOnMouseClicked(mouseEvent1 -> {
                Label backLabel = newLabel("Back");
                VBox vBox = new VBox(newLabel("Change name"), nameField, backLabel);
                nameField.setOnKeyPressed(k -> {
                    if (k.getCode().equals(KeyCode.ENTER)) {
                        player.setName(nameField.getText());
                        setUpVBox(optionLabels);
                    }
                });
                backLabel.setOnMouseClicked(mouseEvent2 ->
                        setUpVBox(optionLabels));
                styleVBox(vBox);
            });
            optionsLabelBack.setOnMouseClicked(mouseEvent1 ->
                    setUpVBox(menuLabels));
            setUpVBox(optionLabels);
        });
        menuLabelExit.setOnMouseClicked(mouseEvent -> System.exit(0));
    }

    private void setUpNameField() {
        nameField.setPromptText(player.getName());
        nameField.setMaxWidth(200);
        nameField.setStyle("-fx-background-color: #242222; -fx-text-inner-color: white");
    }

    public static void updateInventory() {
        soundEngine.play("pickup");
        for (int row = 0; row < inventoryButtons.length; row++) {
            for (int col = 0; col < inventoryButtons[row].length; col++) {
                Button button = inventoryButtons[row][col];
                try {
                    Item item = player.getInventory().get((row*6) + col);

                    if (item != null) {
                        button.setGraphic(Tiles.getImageFor(item.getTileName()));
                        button.setDisable(false);
                    }
                } catch (Exception e) {
                    //This happens when we try to update a button for which there is no item
                    button.setGraphic(Tiles.getImageFor("empty"));
                    button.setDisable(true);
                }
            }
        }
        updateCharacterUI();
    }

    private static void updateCharacterUI() {
        System.out.println(player.getlHandSlot());
        System.out.println(player.getlHandSlot() != null);
        if (player.getHeadSlot() != null) {
            System.out.println("Head slot not NULL");
            headButton.setGraphic(Tiles.getImageFor(player.getHeadSlot().getTileName()));
            headButton.setDisable(false);
        } else {
            headButton.setGraphic(Tiles.getImageFor("empty"));
            headButton.setDisable(true);
        }
        if (player.getlHandSlot() != null) {
            lHandButton.setGraphic(Tiles.getImageFor(player.getlHandSlot().getTileName()));
            lHandButton.setDisable(false);
        } else {
            lHandButton.setGraphic(Tiles.getImageFor("empty"));
            lHandButton.setDisable(true);
        }
        if (player.getTorsoSlot() != null) {
            System.out.println("Torso slot not NULL");
            torsoButton.setGraphic(Tiles.getImageFor(player.getTorsoSlot().getTileName()));
            torsoButton.setDisable(false);
        } else {
            torsoButton.setGraphic(Tiles.getImageFor("empty"));
            torsoButton.setDisable(true);
        }
        if (player.getrHandSlot() != null) {
            rHandButton.setGraphic(Tiles.getImageFor(player.getrHandSlot().getTileName()));
            rHandButton.setDisable(false);
        } else {
            rHandButton.setGraphic(Tiles.getImageFor("empty"));
            rHandButton.setDisable(true);
        }
    }

    private GridPane setUpInventoryPanel() {
        GridPane uiBottom = new GridPane();
        for (int row = 0; row < inventoryButtons.length; row++) {
            for (int col = 0; col < inventoryButtons[row].length; col++) {
                Button btn = new Button();
                int btnCol = col;
                int btnRow = row;
                btn.setOnAction(e -> player.getInventory().get(btnRow + btnCol).use());
                btn.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            player.dropItem(player.getInventory().get(btnRow + btnCol));
                            System.out.println("Dropped");
                            updateInventory();
                        }
                    }
                });
                ImageView img = Tiles.getImageFor("empty");
                btn.setGraphic(img);
                btn.setDisable(true);
                btn.setMinSize(16, 16);
                btn.setPrefSize(28, 28);
                btn.setFocusTraversable(false);
                uiBottom.add(btn, col, row);
                inventoryButtons[row][col] = btn;
            }
        }
        uiBottom.setHgap(2);
        uiBottom.setVgap(2);
        uiBottom.setPrefWidth(200);
        uiBottom.setPadding(new Insets(10));
        return uiBottom;
    }


    private static Button makeCharacterButton() {
        ImageView img = Tiles.getImageFor("empty");
        Button btn = new Button();
        btn.setFocusTraversable(false);
        btn.setMinSize(28, 28);
        btn.setPrefSize(28, 28);
        btn.setGraphic(img);
        btn.setDisable(true);
        return btn;
    }

    private void afterStart(Scene scene) {
        borderPane.setTop(menuBar(name));

        GridPane holder = new GridPane();
        GridPane uiTop = setUpGridPane();
        GridPane uiCharacter = setUpCharacterPane();
        GridPane uiInventory = setUpInventoryPanel();

        BorderPane gameBorderPane = borderPane;
        gameBorderPane.setTop(menuBar(name));

        holder.add(uiTop, 0, 0);
        holder.add(uiCharacter, 0, 1);
        holder.add(uiInventory, 0, 2);
        gameBorderPane.setCenter(canvas);
        gameBorderPane.setRight(holder);

        scene.setOnKeyPressed(this::onKeyPressed);
        //This is the engines fixed time loop for calculating anything that doesn't correlate with the player actions
        KeyFrame enemyMovementFrame = new KeyFrame(Duration.millis(2000), e -> enemyMovement());
        Timeline timeline = new Timeline(enemyMovementFrame);
        timeline.setCycleCount(Animation.INDEFINITE); // loop forever
        timeline.play();

        //Keyboard shortcuts
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
        gameBorderPane.setCenter(canvas);
        borderPane = gameBorderPane;
    }

    private void styleVBox(VBox vBox) {
        vBox.setSpacing(40);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #242222");
        vBox.setMinWidth(map.getWidth() * Tiles.TILE_WIDTH + 200);
        vBox.setMinHeight(map.getHeight() * Tiles.TILE_WIDTH);
        vBox.setMaxWidth(map.getWidth() * Tiles.TILE_WIDTH);
        vBox.setMaxHeight(map.getHeight() * Tiles.TILE_WIDTH);
        borderPane.setCenter(vBox);
    }

    private GridPane setUpCharacterPane() {
        GridPane uiCharacterPane = new GridPane();

        headButton.setOnAction(e -> player.unequip(player.getHeadSlot()));
        torsoButton.setOnAction(e -> player.unequip(player.getTorsoSlot()));
        lHandButton.setOnAction(e -> player.unequip(player.getlHandSlot()));
        rHandButton.setOnAction(e -> player.unequip(player.getrHandSlot()));

        uiCharacterPane.add(headButton, 1, 0);
        uiCharacterPane.add(lHandButton, 0, 1);
        uiCharacterPane.add(torsoButton, 1, 1);
        uiCharacterPane.add(rHandButton, 2, 1);

        uiCharacterPane.setHgap(20);
        uiCharacterPane.setVgap(20);
        uiCharacterPane.setPrefWidth(200);
        uiCharacterPane.setPadding(new Insets(35));
        return uiCharacterPane;
    }

    private GridPane setUpGridPane() {
        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));
        mute.setFocusTraversable(false);
        pickupButton.setFocusTraversable(false);
        ui.add(mute, 1, 0);
        ui.add(new Label("Health: "), 0, 1);
        ui.add(healthLabel, 1, 1);
        ui.add(pickupButton, 0, 2);
        ui.add(inventoryLabel, 0, 4);
        ui.setHgap(10);
        ui.setVgap(10);
        return ui;
    }

    private Reflection setUpReflection() {
        Reflection reflection = new Reflection();
        reflection.setTopOffset(0);
        reflection.setTopOpacity(0.45);
        reflection.setBottomOpacity(0.10);
        reflection.setFraction(0.7);
        return reflection;
    }

    private Label newLabel(String start) {
        Label menuLabelStart = new Label(start);
        setUpLabel(menuLabelStart);
        return menuLabelStart;
    }

    private void setUpLabel(Label menuLabelStart) {
        Reflection reflection = setUpReflection();
        menuLabelStart.setTextFill(Color.WHITE);
        menuLabelStart.setEffect(reflection);
        menuLabelStart.setFont(new Font("Arial", 18));
    }

    private void setUpVBox(List<Label> labels) {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(labels);
        styleVBox(vBox);
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
        Menu volume = new Menu("Volume");
        MenuItem changeName = new MenuItem("Change name");
        CustomMenuItem changeVolume = new CustomMenuItem();

        setupSlider(volume, changeVolume);
        changeName.setOnAction(t -> {
            Stage stage = new Stage();
            HBox hbox = getHBox();
            Button submit = new Button("Submit");
            submit.setOnAction(e -> setNameLabel(name, stage, nameField));
            nameField.setOnKeyPressed(k -> {
                if (k.getCode().equals(KeyCode.ENTER)) {
                    setNameLabel(name, stage, nameField);
                }
            });

            hbox.getChildren().addAll(newLabel("Name: "), nameField, submit);
            Scene scene = new Scene(hbox);
            stage.setScene(scene);
            stage.show();
        });
        menuFile.getItems().addAll(changeName, volume);

        Menu menuSave = new Menu("Saves");
        MenuItem menuItemSave = new MenuItem("Save");
        menuSave.getItems().addAll(menuItemSave, new SeparatorMenuItem()); // TODO: List all saves
        menuItemSave.setOnAction(event -> {
            Stage stage = new Stage();
            HBox hbox = getHBox();
            Button save = new Button("Save");
            Button cancel = new Button("Cancel");
            save.setOnAction(e -> {
                //TODO: Save
            });
            cancel.setOnAction(e -> {
                stage.close();
            });
            TextField saveField = new TextField();
            saveField.setOnKeyPressed(k -> {
                if (k.getCode().equals(KeyCode.ENTER)) {
                    //TODO: Save
                }
            });

            hbox.getChildren().addAll(new Label("File name: "), saveField, save, cancel);
            Scene scene = new Scene(hbox);
            stage.setScene(scene);
            stage.show();
        });
        Menu menuImport = new Menu("Import");
        MenuItem menuItemImport = new MenuItem("Import");
        menuImport.getItems().addAll(menuItemImport);
        menuItemImport.setOnAction(event -> {
            Stage importStage = new Stage();
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(importStage);
            // TODO: Load saves
        });

        menuBar.getMenus().addAll(menuFile, menuSave, menuImport);
        return menuBar;
    }

    private HBox getHBox() {
        HBox hbox = new HBox(5);
        hbox.setPadding(new Insets(25));
        hbox.setMinSize(100, 75);
        return hbox;
    }

    private void setupSlider(Menu volume, CustomMenuItem changeVolume) {
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(40);
        slider.setValue(20);
        slider.setMajorTickUnit(2);
        volume.getItems().addAll(changeVolume);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                soundEngine.changeVolume((float) slider.getValue() - 10);
            }
        });
        changeVolume.setContent(slider);
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
            ai.update();
        }
        refresh();
    }

    private void pickupButtonPressed() {
        Cell playerCell = player.getCell();
        Item item = playerCell.getItem();
        player.pickUpItem(item);
        playerCell.removeItemFromCell();
        pickupButton.setVisible(false);

        //Add to inventory graphically
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

                UUID playerId = map.getPlayer().getUuid();
                dbManager.saveEnemies(aiList, playerId);

                GameState gameState = new GameState(map.getMapString(),Date.valueOf("2021-01-01"), new PlayerModel(player));
                PlayerModel playerModel = new PlayerModel(player);
                gameState.setPlayer(playerModel);
                dbManager.saveGameState(map.getMapString(),Date.valueOf("2021-01-01"), playerModel);
                break;
        }
    }

    private void refresh() {
        if (!player.isAlive()) {
            map = MapLoader.loadMap(0);
            player = map.getPlayer();
            borderPane.setRight(null);
            borderPane.setTop(null);
            setUpVBox(endLabels);
            soundEngine.toggleMute();
        }
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
