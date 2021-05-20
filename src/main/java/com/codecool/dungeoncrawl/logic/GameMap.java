package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.logic.actors.Player;

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


    public void printActors() {
        for (Cell[] cellRow : cells) {
            for (Cell cell : cellRow) {
                if (cell.getActor() != null) {
                    if (cell.getActor().getClass().getSimpleName().equals("Zombie")) {
                        System.out.println(cell.getActor().getTileName());
                    } else {
                        System.out.println(cell.getActor().getClass().getSimpleName());
                    }
                } else if (cell.getItem() != null) {
                    System.out.println(cell.getItem().getClass().getSimpleName());
                } else if (cell.getDoor() != null) {
                    System.out.println(cell.getDoor().getTileName());
                } else if (cell.getType() != null){
                    System.out.println(cell.getTileName() );
                }
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
