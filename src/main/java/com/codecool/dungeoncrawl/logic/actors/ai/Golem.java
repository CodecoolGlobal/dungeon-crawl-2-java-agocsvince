package com.codecool.dungeoncrawl.logic.actors.ai;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.actors.Actor;
import com.codecool.dungeoncrawl.logic.actors.Player;

import java.util.Random;


public class Golem extends AiActor {
    private Random rand = new Random();

    public Golem(Cell cell) {
        super(cell);
        health = 25;
        damage = 2;
        name = "golem";
    }


    @Override
    public void makeMove() {
        int dx = 0;
        int dy = 0;

        dx = rand.nextInt(3)-1;
        if (dx == 0) {
            dy = rand.nextInt(3) - 1;
        }
        Cell neighbour = cell.getNeighbor(dx, dy);
        CellType nbType = neighbour.getType();
        Actor nbActor = neighbour.getActor();

        if (nbType != CellType.WALL || (neighbour.getDoor() != null && neighbour.getDoor().isOpen())) {
            if (nbActor != null) {
                if (nbActor instanceof Player) {
                    attack(nbActor, damage);
                }
            } else {
                cell.setActor(null);
                neighbour.setActor(this);
                cell = neighbour;
            }
        }
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
