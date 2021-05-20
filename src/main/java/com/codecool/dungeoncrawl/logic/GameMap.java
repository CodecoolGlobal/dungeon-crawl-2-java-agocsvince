package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.ai.Golem;
import com.codecool.dungeoncrawl.logic.actors.ai.Skeleton;
import com.codecool.dungeoncrawl.logic.actors.ai.Wraith;
import com.codecool.dungeoncrawl.logic.actors.ai.Zombie;
//import com.codecool.dungeoncrawl.logic.items.Key;
//import com.codecool.dungeoncrawl.logic.items.Sword;

import java.util.ArrayList;
import java.util.Arrays;

public class GameMap {
    private int width;
    private int height;
    private Cell[][] cells;
    private String mapString = "#############";


    public void getMapObjectsToArray(int width, int height) {
        ArrayList<String> allElementsInStringArrayList = new ArrayList<String>();
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (cell.getActor() != null) {
                    if (cell.getActor().getClass().getSimpleName().equals("Zombie")) {
                        allElementsInStringArrayList.add(cell.getActor().getTileName());
                    } else {
                        allElementsInStringArrayList.add(cell.getActor().getClass().getSimpleName());
                    }
                } else if (cell.getItem() != null) {
                    allElementsInStringArrayList.add(cell.getItem().getClass().getSimpleName());
                } else if (cell.getDoor() != null) {
                    allElementsInStringArrayList.add(cell.getDoor().getTileName());
                } else if (cell.getType() != null) {
                    allElementsInStringArrayList.add(cell.getTileName());
                }
            }
        }
        replaceTileNameToALetter(allElementsInStringArrayList);
        convertMapArrayListToMultiDimArray(allElementsInStringArrayList, width, height);
    }

    private void replaceTileNameToALetter(ArrayList<String> allElementsInStringArrayList) {
        for (int i = 0; i < allElementsInStringArrayList.size(); i++) {
            switch (allElementsInStringArrayList.get(i)) {
                case "empty":
                    allElementsInStringArrayList.set(i, " ");
                    break;
                case "wall":
                    allElementsInStringArrayList.set(i, "#");
                    break;
                case "floor":
                    allElementsInStringArrayList.set(i, ".");
                    break;
                case "Skeleton":
                    allElementsInStringArrayList.set(i, "s");
                    break;
                case "Golem":
                    allElementsInStringArrayList.set(i, "g");
                    break;
                case "Player":
                    allElementsInStringArrayList.set(i, "@");
                    break;
                case "Key":
                    allElementsInStringArrayList.set(i, "k");
                    break;
                case "Sword":
                    allElementsInStringArrayList.set(i, "w");
                    break;
                case "closedDoor":
                    allElementsInStringArrayList.set(i, "d");
                    break;
                case "openedDoor":
                    allElementsInStringArrayList.set(i, ".");   // TODO: add opendoor to Maploader, than change here to "D"
                    break;
                case "Wraith":
                    allElementsInStringArrayList.set(i, "W");
                    break;
                case "sleepingZombie":
                    allElementsInStringArrayList.set(i, "z");
                    break;
                case "zombie":
                    allElementsInStringArrayList.set(i, "z");   // TODO: add awakezombie to Maploader, than change here to "Z"
                    break;
            }
        }
    }

    private void convertMapArrayListToMultiDimArray(ArrayList<String> allElementsInStringArrayList, int width, int height) {
        char[] allElementsInCharList = convertStringArrayListToCharArray(allElementsInStringArrayList);
        char[][] StringArray = new char[height][width];
        int c = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                StringArray[y][x] = allElementsInCharList[c];
                c++;
            }
        }
    }

    private char[] convertStringArrayListToCharArray(ArrayList<String> allElementsInStringArrayList) {
        char[] allElementsInCharList = new char[allElementsInStringArrayList.size()];
        for (int i = 0; i < allElementsInStringArrayList.size(); i++) {
            allElementsInCharList[i] = allElementsInStringArrayList.get(i).charAt(0);
        }
        return allElementsInCharList;
    }

    private Player player;

    public GameMap(int width, int height, CellType defaultCellType) {
        this.width = width;
        this.height = height;
        cells = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(this, x, y, defaultCellType);
            }
        }
    }

    public Cell getCell(int x, int y) {
        try {
            return cells[x][y];
        } catch (Exception exception) {
            return null;
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getMapString() {
        return mapString;
    }

    public void setMapString(String mapString) {
        this.mapString = mapString;
    }
}
