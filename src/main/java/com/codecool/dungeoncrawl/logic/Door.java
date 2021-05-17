package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.GameEngine;

public class Door implements Drawable {
    private String name = "closedDoor";
    private final Cell cell;
    private boolean isOpen = false;
    private CellType cellType;

    public Door(Cell cell) {
        this.cell = cell;
        this.cell.setDoor(this);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public Cell getCell() {
        return cell;
    }

    public void openTheDoor() {
        this.name = "openedDoor";
        GameEngine.soundEngine.play("doorOpen");
        isOpen = true;
    }

    @Override
    public String getTileName() {
        return name;
    }
}
