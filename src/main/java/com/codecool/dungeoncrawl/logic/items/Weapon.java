package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.logic.Cell;

import java.util.HashMap;

public class Weapon extends Equipable {


    private final static HashMap<ITEM_NAME, WEAPON_STAT> itemWeaponStatHashMap = new HashMap<>();

    static {
        itemWeaponStatHashMap.put(ITEM_NAME.SWORD, WEAPON_STAT.SWORD);
        itemWeaponStatHashMap.put(ITEM_NAME.KNIFE, WEAPON_STAT.KNIFE);
    }

    enum WEAPON_STAT {

        SWORD(10),
        KNIFE(4);

        public final int damage;

        WEAPON_STAT(int damage) {
            this.damage = damage;
        }
    }

    @Override
    public HashMap<String, Integer> getStats() {
        HashMap<String, Integer> returnMap = new HashMap<>();
        WEAPON_STAT stat = itemWeaponStatHashMap.get(itemType);
        returnMap.put("Damage", stat.damage);
        return returnMap;
    }

    public Weapon(ITEM_NAME item, Cell cell) {
        super(item, cell);
        System.out.println("Setting equip slot to HAND");
        equipSlot = EQUIP_POSITION.HAND;
    }
}
