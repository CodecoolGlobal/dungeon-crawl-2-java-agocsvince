package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

public class Equipable extends Item {
    public Equipable(ITEM item, Cell cell) {
        super(item, cell);
    }

    protected EQUIP_POSITION equipSlot = EQUIP_POSITION.NONE;

    enum EQUIP_POSITION{
        HAND,
        HEAD,
        TORSO,
        NONE
    }

    @Override
    public void use() {
        player.interact(this);
    }
}
