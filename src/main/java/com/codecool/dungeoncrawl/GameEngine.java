package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.dao.GameDatabaseManager;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import com.codecool.dungeoncrawl.logic.items.Item;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GameEngine extends Application {
    public static ArrayList<AiActor> aiList = new ArrayList<AiActor>();
    GameDatabaseManager dbManager;


    public static SoundEngine soundEngine = new SoundEngine();
    GameMap map = MapLoader.loadMap();
    Player player = map.getPlayer();
    Canvas canvas = new Canvas(
            map.getWidth() * Tiles.TILE_WIDTH,
            map.getHeight() * Tiles.TILE_WIDTH);
    GraphicsContext context = canvas.getGraphicsContext2D();
    Label healthLabel = new Label();
    Label inventoryLabel = new Label();
    BorderPane borderPane = new BorderPane();
    Button pickupButton = new Button("Pick up item (E)");
    Button mute = new Button("Mute");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        setupDbManager();
        pickupButton.setFocusTraversable(false);
        pickupButton.setOnAction(actionEvent -> pickupButtonPressed());

        Label name = new Label("Player");
        Label inventory = new Label("Inventory: ");
        mute.setFocusTraversable(false);
        mute.setOnAction(actionEvent -> toggleMute());

        GridPane ui = setUpGridPane(name, inventory);

        borderPane.setTop(menuBar(name));

        // Menu
        Label menuLabelStart = newLabel("Start");
        Label menuLabelOptions = newLabel("Options");
        Label menuLabelExit = newLabel("Exit");
        List<Label> menuLabels = Arrays.asList(menuLabelStart, menuLabelOptions, menuLabelExit);
        //Options
        Label optionsLabelName = newLabel("Name");
        Label optionsLabelBack = newLabel("Back");
        List<Label> optionLabels = Arrays.asList(optionsLabelName, optionsLabelBack);
        //Menu actions
        menuLabelStart.setOnMouseClicked(mouseEvent -> borderPane.setCenter(canvas));
        menuLabelOptions.setOnMouseClicked(mouseEvent -> {
            //Option actions
            optionsLabelBack.setOnMouseClicked(mouseEvent1 ->
                    setUpVBox(ui, borderPane, menuLabels));
            setUpVBox(ui, borderPane, optionLabels);
        });
        menuLabelExit.setOnMouseClicked(mouseEvent -> System.exit(0));

        setUpVBox(ui, borderPane, menuLabels);

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

    private GridPane setUpGridPane(Label name, Label inventory) {
        GridPane ui = new GridPane();
        ui.setPrefWidth(200);
        ui.setPadding(new Insets(10));
        ui.add(name, 0, 0);
        ui.add(mute, 1, 0);
        ui.add(new Label("Health: "), 0, 1);
        ui.add(healthLabel, 1, 1);
        ui.add(pickupButton, 0, 2);
        ui.add(inventory, 0, 3);
        ui.add(inventoryLabel, 0, 4);
        ui.setHgap(10);
        ui.setVgap(10);
        return ui;
    }

    private Reflection setUpReflection() {
        Reflection reflection = new Reflection();
        reflection.setTopOffset(0);
        reflection.setTopOpacity(0.75);
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

    private void setUpVBox(GridPane ui, BorderPane borderPane, List<Label> labels) {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(labels);
        vBox.setSpacing(40);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #242222");
        borderPane.setCenter(vBox);
        vBox.setMinWidth(map.getWidth() * Tiles.TILE_WIDTH);
        vBox.setMinHeight(map.getHeight() * Tiles.TILE_WIDTH);
        vBox.setMaxWidth(map.getWidth() * Tiles.TILE_WIDTH);
        vBox.setMaxHeight(map.getHeight() * Tiles.TILE_WIDTH);
        borderPane.setRight(ui);
    }

    private void setupDbManager () {
            dbManager = new GameDatabaseManager();
            try {
                dbManager.setup();
            } catch (SQLException ex) {
                System.out.println("Cannot connect to database.");
            }
        }

        private void toggleMute () {
            soundEngine.toggleMute();
            if (soundEngine.isMuted()) {
                mute.setText("Unmute");
            } else {
                mute.setText("Mute");
            }
        }


        private MenuBar menuBar (Label name){
            MenuBar menuBar = new MenuBar();
            Menu menuFile = new Menu("Settings");
            Menu volume = new Menu("Volume");
            CustomMenuItem changeVolume = new CustomMenuItem();
            setupSlider(volume, changeVolume);
            MenuItem changeName = new MenuItem("Change name");
            changeName.setOnAction(t -> {
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
            menuFile.getItems().addAll(changeName, volume);
            menuBar.getMenus().addAll(menuFile);
            return menuBar;
        }

    private void setupSlider(Menu volume, CustomMenuItem changeVolume) {
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(40);
        slider.setValue(20);
        slider.setMajorTickUnit(2);
        volume.getItems().addAll(changeVolume);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<?extends Number> observable, Number oldValue, Number newValue){
                soundEngine.changeVolume((float) slider.getValue()-10);
            }
        });
        changeVolume.setContent(slider);
    }

    private void setNameLabel (Label name, Stage stage, TextField textField){
            player.setName(textField.getText());
            name.setText(player.getName());
            stage.close();
        }

        private void enemyMovement () {
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


        private void pickupButtonPressed () {
            Cell playerCell = player.getCell();
            Item item = playerCell.getItem();
            player.pickUpItem(item);
            playerCell.removeItemFromCell();
            pickupButton.setVisible(false);

            System.out.println(item.getName());
            inventoryLabel.setText(inventoryLabel.getText() + item.getName() + " ");
        }

        private void onKeyPressed (KeyEvent keyEvent){
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

        private void refresh () {
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

        private void onKeyReleased (KeyEvent keyEvent){
            KeyCombination exitCombinationMac = new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN);
            KeyCombination exitCombinationWin = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
            if (exitCombinationMac.match(keyEvent)
                    || exitCombinationWin.match(keyEvent)
                    || keyEvent.getCode() == KeyCode.ESCAPE) {
                exit();
            }
        }

        private void exit () {
            try {
                stop();
            } catch (Exception e) {
                System.exit(1);
            }
            System.exit(0);
        }

    }
