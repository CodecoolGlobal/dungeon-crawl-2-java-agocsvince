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
        for (ITEM_NAME item : ITEM_NAME.values()) {
            if (item.id == itemType.id) {
                name = item.tileName;
                break;
            }
        }
        return name;
    }

    public enum ITEM_NAME {
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

        ITEM_NAME(int id, String tileName) {
            this.id = id;
            this.tileName = tileName;
        }
    }


    public Item(ITEM_NAME item, Cell cell) {
        this.itemType = item;
        this.cell = cell;
        cell.setItem(this);
        equipSlot = Equipable.EQUIP_POSITION.NONE;
    }

    protected Equipable.EQUIP_POSITION equipSlot;
    protected final ITEM_NAME itemType;
    private Cell cell;

    public Equipable.EQUIP_POSITION getEquipSlot() {
        return equipSlot;
    }

    public int getItemID() {
        return itemType.id;
    }

    public void use(){
        System.out.println("using");
        GameEngine.getPlayer().interact(this);
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
