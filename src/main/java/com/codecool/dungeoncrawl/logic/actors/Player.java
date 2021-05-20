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
        health = 20;
        maxHealth = health;
        damage = 1;
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
                    GameEngine.updateInventory();
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
            item.setCell(null);
            GameEngine.updateInventory();
        }
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void dropItem(Item item) {
        if (cell.getItem() == null) {
            cell.setItem(item);
            item.setCell(cell);
            inventory.remove(item);
            GameEngine.updateInventory();
        }
    }

    public void interact(Item item) {
        System.out.println(item.getClass().getSimpleName());
        if (item instanceof Armor) {
            equip(item);
            System.out.println("Equipping armor");
        } else if (item instanceof Weapon) {
            System.out.println("Equipping weapon");
            equip(item);
        } else if (item instanceof HealItem) {
            heal(item.getStats().get("Healing"));
            GameEngine.soundEngine.play("potionDrink");
            inventory.remove(item);
        }
        GameEngine.updateInventory();
    }

    public Item getHeadSlot() {
        return headSlot;
    }

    public Item getlHandSlot() {
        return lHandSlot;
    }

    public Item getrHandSlot() {
        return rHandSlot;
    }

    public Item getTorsoSlot() {
        return torsoSlot;
    }

    public int getArmor() {
        return armor;
    }

    public void unequip(Item item) {
        System.out.println("Unequiping " + item);
        if (inventory.size() < maxInventorySize) {
            System.out.println("Inventory OK");
            switch (item.getEquipSlot()) {
                case TORSO:
                    removeArmor(Equipable.EQUIP_POSITION.TORSO);
                    break;
                case HEAD:
                    removeArmor(Equipable.EQUIP_POSITION.HEAD);
                    break;
                case HAND:
                    removeWeapon((Weapon) item);
                    break;
            }
        }
        GameEngine.updateInventory();
    }

    public void equip(Item item) {
        System.out.println(item.getEquipSlot());
        if (item.getEquipSlot() == Equipable.EQUIP_POSITION.HEAD) {
            System.out.println("Head equipment going on");
            if (headSlot == null) {
                equipArmor((Armor) item, Equipable.EQUIP_POSITION.HEAD);
            } else {
                if (inventory.size() < maxInventorySize) {
                    removeArmor(Equipable.EQUIP_POSITION.HEAD);
                    equipArmor((Armor) item, Equipable.EQUIP_POSITION.HEAD);
                }
            }
        } else if (item.getEquipSlot() == Equipable.EQUIP_POSITION.TORSO) {
            System.out.println("Torso equipment going on");
            if (torsoSlot == null) {
                equipArmor((Armor) item, Equipable.EQUIP_POSITION.TORSO);
            } else {
                if (inventory.size() < maxInventorySize) {
                    removeArmor(Equipable.EQUIP_POSITION.TORSO);
                    equipArmor((Armor) item, Equipable.EQUIP_POSITION.TORSO);
                }
            }
        } else if (item.getEquipSlot() == Equipable.EQUIP_POSITION.HAND) {
            if (lHandSlot == null) {
                lHandSlot = equipWeapon((Weapon) item);
                System.out.println("lHandSlot: " + lHandSlot);
            } else if (rHandSlot == null) {
                rHandSlot = equipWeapon((Weapon) item);
                System.out.println("rHandSlot: " + rHandSlot);
            } else {
                if (inventory.size() < maxInventorySize) {
                    inventory.add(rHandSlot);
                    removeWeapon((Weapon) rHandSlot);
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

    private void removeWeapon(Weapon weapon) {
        damage -= weapon.getStats().get("Damage");
        inventory.add(weapon);
        if (weapon == lHandSlot) {
            lHandSlot = null;
        } else {
            rHandSlot = null;
        }
    }

    private void equipArmor(Armor armorItem, Equipable.EQUIP_POSITION position) {

        inventory.remove(armorItem);
        armor += armorItem.getStats().get("Armour");
        System.out.println("Position is " +position);
        switch (position) {
            case TORSO:
                torsoSlot = armorItem;
                break;
            case HEAD:
                headSlot = armorItem;
                break;
        }
    }

    private void removeArmor(Equipable.EQUIP_POSITION position) {
        switch (position) {
            case HEAD:
                inventory.add(headSlot);
                armor -= headSlot.getStats().get("Armour");
                headSlot = null;
                break;
            case TORSO:
                inventory.add(torsoSlot);
                armor -= torsoSlot.getStats().get("Armour");
                torsoSlot = null;
                break;
        }
    }

    @Override
    public void takeDamage(int damage) {
        damage -= armor;
        damage = Math.max(damage, 1);
        health -= damage;
        health = Math.max(health, 0);
        if (health <= 0) {
            die();
        }
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
}
