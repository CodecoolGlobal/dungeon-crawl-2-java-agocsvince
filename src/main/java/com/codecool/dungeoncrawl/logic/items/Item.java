package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.Drawable;

public abstract class Item implements Drawable {
    private Cell cell;
    protected int EncreasingHealth;
    protected int EncreasingDamage;

    public String getName() {
        return "Default";
    }

    public Item(Cell cell) {
        this.cell = cell;
        this.cell.setItem(this);
    }

    public Cell getCell() {
        return cell;
    }

    public int getX() {
        return cell.getX();
    }

    public int getY() {
        return cell.getY();
    }

    public int getIncreaseInHealth() {
        return EncreasingHealth;
    }

    public int getIncreaseInDamage() {
        return EncreasingDamage;
    }
}
