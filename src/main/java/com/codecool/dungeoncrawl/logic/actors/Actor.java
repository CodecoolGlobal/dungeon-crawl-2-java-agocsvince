package com.codecool.dungeoncrawl.logic.actors;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.Drawable;

public abstract class Actor implements Drawable {
    protected Cell cell;
    protected int health;
    protected int damage;
    protected boolean isAlive = true;

    public Actor(Cell cell) {
        this.cell = cell;
        this.cell.setActor(this);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
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

    public void die() {
        isAlive = false;
    }

    public void takeDamage(int damage) {
        health -= damage;
        health = Math.max(health, 0);
        if (health <= 0){
            die();
        }
    }

    public void attack(Actor actor, int damage) {
        actor.takeDamage(damage);
        GameEngine.soundEngine.play("attack");
    }
}
