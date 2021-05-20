package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import com.codecool.dungeoncrawl.logic.items.*;

import java.util.*;

public class Player extends Actor {
    private UUID uuid = UUID.randomUUID();
    private final ArrayList<Item> inventory = new ArrayList<>();
    private String name;
    private final static String[] developers = new String[]{"Lehel", "Tomi", "Mate", "Vince"};

    private Item headSlot;
    private Item lHandSlot;
    private Item rHandSlot;
    private Item torsoSlot;
    private int armor = 0;
    private int maxInventorySize = 24;
    private int maxHealth;

    public Player(Cell cell) {
        super(cell);
        health = 15;
        maxHealth = health;
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
        if (cell.getNeighbor(dx, dy) != null) {
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
        attack(enemy, damage);
        if (!enemy.isAlive()) {
            cell.getNeighbor(dx, dy).setActor(null);
        } else {
            takeDamage(1);
        }
    }

    public void pickUpItem(Item item) {
        if (inventory.size() < maxInventorySize) {
            inventory.add(item);
            GameEngine.soundEngine.play("pickup");
            GameEngine.updateInventory();
        }
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void dropItem(Item item) {
        if (cell.getItem() == null) {
            cell.setItem(item);
            inventory.remove(item);
            System.out.println(inventory);
            GameEngine.soundEngine.play("pickup");
        }
    }

    public void interact(Item item) {
        if (item instanceof Armor) {
            equip(item);
        } else if (item instanceof Weapon) {
            equip(item);
        } else if (item instanceof HealItem) {
            heal(item.getStats().get("Healing"));
        }
    }

    public void equip(Item item) {
        if (item.getEquipSlot() == Equipable.EQUIP_POSITION.HEAD) {
            if (headSlot == null) {
                equipArmor((Armor) item);
            } else {
                if (inventory.size() < maxInventorySize) {
                    removeArmor();
                    equipArmor((Armor) item);
                }
            }
        } else if (item.getEquipSlot() == Equipable.EQUIP_POSITION.TORSO) {
            if (torsoSlot == null) {
                equipArmor((Armor) item);
            } else {
                if (inventory.size() < maxInventorySize) {
                    removeArmor();
                    equipArmor((Armor) item);
                }
            }
        } else if (item.getEquipSlot() == Equipable.EQUIP_POSITION.HAND) {
            if (lHandSlot == null) {
                lHandSlot = equipWeapon((Weapon) item);
            } else if (rHandSlot == null) {
                rHandSlot = equipWeapon((Weapon) item);
            } else {
                if (inventory.size() < maxInventorySize) {
                    inventory.add(rHandSlot);
                    rHandSlot = removeWeapon((Weapon) rHandSlot);
                    rHandSlot = equipWeapon((Weapon) item);
                }
            }
        }
    }


    private Item equipWeapon(Weapon weapon) {
        damage += weapon.getStats().get("Damage");
        inventory.remove(weapon);
        System.out.println("Equipping weapon");
        return weapon;
    }

    private Item removeWeapon(Weapon weapon) {
        damage -= weapon.getStats().get("Damage");
        System.out.println("Dropping weapon");
        return null;
    }

    private void equipArmor(Armor armorItem) {
        headSlot = armorItem;
        inventory.remove(armorItem);
        armor += armorItem.getStats().get("Armour");
    }

    private void removeArmor() {
        inventory.add(headSlot);
        armor -= headSlot.getStats().get("Armour");
    }

    public void heal(int healAmount) {
        health += healAmount;
        health = Math.min(health, maxHealth);
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
