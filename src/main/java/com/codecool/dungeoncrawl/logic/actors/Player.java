package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import com.codecool.dungeoncrawl.logic.items.*;

import java.rmi.server.UID;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Player extends Actor {
    private UUID uuid = UUID.randomUUID();
    private final Set<Item> inventory = new HashSet<>();
    private String name;
    private final static String[] developers = new String[]{"Lehel", "Tomi", "Mate", "Vince"};

    private Item headSlot;
    private Item lHandSlot;
    private Item rHandSlot;
    private Item torsoSlot;

    public Player(Cell cell) {
        super(cell);
        health = 10;
        damage = 5;
        this.name = "developer";
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void move(int dx, int dy) {
        if (cell.getNeighbor(dx, dy) != null){
            Cell neighbor = cell.getNeighbor(dx, dy);
            if (neighbor.getType() != CellType.WALL && !Arrays.asList(developers).contains(this.name)) {
                move(dx, dy, neighbor);
            } else if (Arrays.asList(developers).contains(this.name)) {
                move(dx, dy, neighbor);
            }
        }
    }

    public String getName() {
        return name;
    }

    private void move(int dx, int dy, Cell neighbor) {
        if (neighbor.getActor() != null && neighbor.getActor() instanceof AiActor) {
            fightEngine(dx, dy, neighbor);
            return;
        }
        if (neighbor.getDoor() != null && neighbor.getDoor().getTileName().equals("closedDoor")) {
            for (Item item : inventory) {
                if (item.getItemID() == Item.ITEM_NAME.KEY_YELLOW.id) {
                    neighbor.openDoor(item);
                    inventory.remove(item);
                    return;
                }
            }
            return;
        } else if (neighbor.getDoor() != null && neighbor.getDoor().getTileName().equals("openedDoor")) {
            // TODO: Change level
        }
        GameEngine.soundEngine.play("footstep");
        Cell nextCell = cell.getNeighbor(dx, dy);
        cell.setActor(null);
        nextCell.setActor(this);
        cell = nextCell;
    }

    private void fightEngine(int dx, int dy, Cell neighbor) {
        AiActor enemy = (AiActor) neighbor.getActor();
        attack(enemy,damage);
        if (!enemy.isAlive()) {
            cell.getNeighbor(dx, dy).setActor(null);
        } else {
            takeDamage(1);
        }
    }

    public void pickUpItem(Item item) {
        inventory.add(item);
        GameEngine.soundEngine.play("pickup");
    }

    public void dropItem(Item item){
        if (cell.getItem() == null) {
            inventory.remove(item);
            GameEngine.soundEngine.play("pickup");
        }
    }

    public void interact(Item item){
        if (item instanceof  Armor){
            equip(item);
        } else if (item instanceof Weapon){

        } else if (item instanceof HealItem){

        }
    }

    public void equip(Item item){

    }

    public void heal(int healAmount){

    }

    public String getTileName() {
        return "player";
    }

    @Override
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
