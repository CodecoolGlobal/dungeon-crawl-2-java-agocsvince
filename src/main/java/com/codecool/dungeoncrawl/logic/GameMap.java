package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.actors.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class GameMap {
    private int width;
    private int height;
    private Cell[][] cells;
    private String mapString = "#############";


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


    public void getMapObjectsToArray(int width, int height) {
        ArrayList<String> allElementsInStringArrayList = new ArrayList<String>();
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (cell.getActor() != null) {
                    if (cell.getActor().getClass().getSimpleName().equals("Zombie")) {
                        allElementsInStringArrayList.add(cell.getActor().getTileName());
//                        System.out.println(cell.getActor().getTileName());
                    } else {
                        allElementsInStringArrayList.add(cell.getActor().getClass().getSimpleName());
//                        System.out.println(cell.getActor().getClass().getSimpleName());
                    }
                } else if (cell.getItem() != null) {
                    allElementsInStringArrayList.add(cell.getItem().getClass().getSimpleName());
//                    System.out.println(cell.getItem().getClass().getSimpleName());
                } else if (cell.getDoor() != null) {
                    allElementsInStringArrayList.add(cell.getDoor().getTileName());
//                    System.out.println(cell.getDoor().getTileName());
                } else if (cell.getType() != null) {
                    allElementsInStringArrayList.add(cell.getTileName());
//                    System.out.println(cell.getTileName());
                }
            }
        }
        convertMapArrayListToMultiDimArray(allElementsInStringArrayList, width, height);
    }

    private void convertMapArrayListToMultiDimArray(ArrayList<String> allElementsInStringArrayList, int width, int height) {
        String[][] StringArray = new String[width][height];
        int c = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
//            String[] row = new String[width];
                StringArray[x][y] = allElementsInStringArrayList.get(c);
                c++;
            }
//                    for (String s : allElementsInStringArrayList) {

        }
        System.out.println(Arrays.deepToString(StringArray));
    }

        public Cell getCell ( int x, int y){
            try {
                return cells[x][y];
            } catch (Exception exception) {
                return null;
            }
        }

        public void setPlayer (Player player){
            this.player = player;
        }

        public Player getPlayer () {
            return player;
        }

        public int getWidth () {
            return width;
        }

        public int getHeight () {
            return height;
        }

        public String getMapString () {
            return mapString;
        }

        public void setMapString (String mapString){
            this.mapString = mapString;
        }
    }
