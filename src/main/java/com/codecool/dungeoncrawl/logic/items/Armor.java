package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

import java.util.HashMap;

public class Armor extends Equipable {

    private final static HashMap<ITEM_NAME, ARMOR_STAT> itemArmorStatHashMap = new HashMap<>();

    static {
        itemArmorStatHashMap.put(ITEM_NAME.ARMOR_LIGHT, ARMOR_STAT.ARMOR_LIGHT_STAT);
        itemArmorStatHashMap.put(ITEM_NAME.ARMOR_MEDIUM, ARMOR_STAT.ARMOR_MEDIUM_STAT);
        itemArmorStatHashMap.put(ITEM_NAME.ARMOR_HEAVY, ARMOR_STAT.ARMOR_HEAVY_STAT);

        itemArmorStatHashMap.put(ITEM_NAME.HELMET_LIGHT, ARMOR_STAT.HELMET_LIGHT_STAT);
        itemArmorStatHashMap.put(ITEM_NAME.HELMET_MEDIUM, ARMOR_STAT.HELMET_MEDIUM_STAT);
        itemArmorStatHashMap.put(ITEM_NAME.HELMET_HEAVY, ARMOR_STAT.HELMET_HEAVY_STAT);
    }

    enum ARMOR_STAT {

        HELMET_LIGHT_STAT(1),
        HELMET_MEDIUM_STAT(2),
        HELMET_HEAVY_STAT(3),

        ARMOR_LIGHT_STAT(2),
        ARMOR_MEDIUM_STAT(4),
        ARMOR_HEAVY_STAT(6);

        public final int armorValue;

        ARMOR_STAT(int armorValue) {
            this.armorValue = armorValue;
        }
    }

    @Override
    public HashMap<String, Integer> getStats() {
        HashMap<String,Integer> returnMap = new HashMap<>();
        ARMOR_STAT stat = itemArmorStatHashMap.get(itemType);
        returnMap.put("Armour", stat.armorValue);
        return returnMap;
    }

    public Armor(ITEM_NAME item, Cell cell) {
        super(item, cell);
        //Setup equip slot
    }
}
