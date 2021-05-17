package com.codecool.dungeoncrawl.logic.actors.ai;

import com.codecool.dungeoncrawl.logic.Cell;

public class Skeleton extends AiActor {
    public Skeleton(Cell cell) {
        super(cell);
        health = 11;
        damage = 2;
        name = "skeleton";
    }

    @Override
    public void makeMove() {

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
