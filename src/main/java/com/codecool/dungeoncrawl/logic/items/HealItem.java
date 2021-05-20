package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;

import java.util.HashMap;

public class HealItem extends Item {

    enum HEAL_ITEM_STAT {

        POTION_HEALTH(10);

        private final int healRegen;

        HEAL_ITEM_STAT(int healRegen) {
            this.healRegen = healRegen;
        }
    }

    @Override
    public HashMap<String, Integer> getStats() {
        HashMap<String, Integer> returnMap = new HashMap<>();
        returnMap.put("Healing", HEAL_ITEM_STAT.POTION_HEALTH.healRegen);
        return returnMap;
    }

    public HealItem(ITEM_NAME item, Cell cell) {
        super(item, cell);
    }
}
