package com.codecool.dungeoncrawl.logic.items;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.Drawable;
import com.codecool.dungeoncrawl.logic.actors.Player;

import java.util.HashMap;

public class Item implements Drawable {

    @Override
    public String getTileName() {
        String name = null;
        for (ITEM item : ITEM.values()) {
            if (item.id == itemType.id) {
                name = item.tileName;
                break;
            }
        }
        return name;
    }

    public enum ITEM {
        SWORD(0, "sword"),
        KNIFE(1, "knife"),

        HELMET_LIGHT(2, "lHelmet"),
        HELMET_MEDIUM(3, "mHelmet"),
        HELMET_HEAVY(4, "hHelmet"),
        ARMOR_LIGHT(5, "lArmor"),
        ARMOR_MEDIUM(6, "mArmor"),
        ARMOR_HEAVY(7, "hArmor"),

        POTION_HEALTH(8, "healthPotion"),

        KEY_YELLOW(9, "yellowKey"),
        KEY_RED(10, "redKey");


        public final int id;
        public final String tileName;

        ITEM(int id, String tileName) {
            this.id = id;
            this.tileName = tileName;
        }
    }


    public Item(ITEM item, Cell cell) {
        this.itemType = item;
        this.cell = cell;
        cell.setItem(this);
    }

    protected Player player = GameEngine.getPlayer();
    protected Equipable.EQUIP_POSITION equipSlot = Equipable.EQUIP_POSITION.NONE;
    protected final ITEM itemType;
    private Cell cell;

    public int getItemID() {
        return itemType.id;
    }

    public void use(){

    }

    public HashMap<String, Integer> getStats() {
        return new HashMap<>();
    }

    public String getName() {
        return "Default";
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

}
