package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

public class Sword extends Item {
    private final int increaseInHealth = 0;
    private final int increaseInDamage = 5;

    @Override
    public String getName() {
        return "Sword";
    }

    public Sword(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "sword";
    }

    @Override
    public int getIncreaseInHealth() {
        return increaseInHealth;
    }

    @Override
    public int getIncreaseInDamage() {
        return increaseInDamage;
    }


}
