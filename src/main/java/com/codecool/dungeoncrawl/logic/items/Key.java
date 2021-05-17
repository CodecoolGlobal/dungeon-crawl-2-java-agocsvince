package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

public class Key extends Item {
    private final int EncreasingHealth = 0;
    private final int EncreasingDamage = 0;

    @Override
    public String getName() {
        return "Key";
    }

    public Key(Cell cell) {
        super(cell);
    }

    @Override
    public String getTileName() {
        return "key";
    }

    @Override
    public int getIncreaseInHealth() {
        return EncreasingHealth;
    }

    @Override
    public int getIncreaseInDamage() {
        return EncreasingDamage;
    }
}
