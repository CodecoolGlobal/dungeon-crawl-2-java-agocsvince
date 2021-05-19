package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import com.codecool.dungeoncrawl.logic.items.Item;
import com.codecool.dungeoncrawl.logic.items.Key;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Player extends Actor {
    private final Set<Item> inventory = new HashSet<>();
    private String name;
    private final String[] developers = new String[]{"Lehel", "Tomi", "Mate", "Vince"};

    public Player(Cell cell) {
        super(cell);
        health = 10;
        damage = 5;
        this.name = "developer";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void move(int dx, int dy) {
        Cell neighbor = cell.getNeighbor(dx, dy);
        if (neighbor.getType() != CellType.WALL && !Arrays.asList(developers).contains(this.name)) {
            move(dx, dy, neighbor);
        } else if (Arrays.asList(developers).contains(this.name)) {
            move(dx, dy, neighbor);
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
                if (item instanceof Key) {
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
        if (!item.getTileName().equals("key"))
            addItemBoost(item);
        GameEngine.soundEngine.play("pickup");
    }

    private void addItemBoost(Item item) {
        setHealth(health += item.getIncreaseInHealth());
        setDamage(damage += item.getIncreaseInDamage());
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
