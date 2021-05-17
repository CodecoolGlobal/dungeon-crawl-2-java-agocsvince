package com.codecool.dungeoncrawl.logic.actors.ai;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.actors.Actor;
import com.codecool.dungeoncrawl.logic.actors.Player;

public class Zombie extends AiActor {

    GameMap map;
    Cell startPosition;
    Boolean triggered = false;
    int triggerDistance = 3;

    public Zombie(Cell cell, GameMap map) {
        super(cell);
        this.map = map;
        damage = 3;
        health = 35;
        startPosition = cell;
        name = "sleepingZombie";
    }


    private void wake(){
        triggered = true;
        name = "zombie";
        GameEngine.soundEngine.play("zombie");
    }

    @Override
    public void die() {
        isAlive = false;
        GameEngine.soundEngine.play("zombie");
    }


    @Override
    public void makeMove() {

        Player target = map.getPlayer();
        int targetX = target.getCell().getX();
        int targetY = target.getCell().getY();
        int relX = targetX - cell.getX();
        int relY = targetY - cell.getY();


        if (!triggered) {
            //Trigger if player is too close
            if (Math.abs(relY) <= triggerDistance && Math.abs(relX) <= triggerDistance) {
                wake();
            }
        } else {
            int dx = 0;
            int dy = 0;
            if (Math.abs(relX) > Math.abs(relY)) {

                dx = Math.min(Math.max(-1, relX), 1);
                System.out.println("dx: " + dx);
            } else {
                dy = Math.min(Math.max(-1, relY), 1);
                System.out.println("dy: " + dy);
            }
            Cell nextCell = cell.getNeighbor(dx, dy);
            CellType nbType = nextCell.getType();
            Actor nbActor = nextCell.getActor();

            if (nbActor != null) {
                if (nbActor instanceof Player) {
                    attack(nbActor, damage);
                    return;
                }
            } else {
                if (nextCell.getDoor() != null) {
                    if (nextCell.getDoor().isOpen()) {
                        System.out.println(nextCell.getDoor().isOpen());
                        cell.removeActorFromCell();
                        nextCell.setActor(this);
                        cell = nextCell;
                        return;
                    }
                } else if (nbType != CellType.WALL) {
                    cell.removeActorFromCell();
                    nextCell.setActor(this);
                    cell = nextCell;
                    return;
                }
            }
            nextCell = cell.getNeighbor(dy, dx * -1);
            nbType = nextCell.getType();
            nbActor = nextCell.getActor();
            if (nbActor == null) {
                if (nextCell.getDoor() != null) {
                    if (nextCell.getDoor().isOpen()) {
                        System.out.println(nextCell.getDoor().isOpen());
                        cell.removeActorFromCell();
                        nextCell.setActor(this);
                        cell = nextCell;
                    }
                } else if (nbType != CellType.WALL) {
                    cell.removeActorFromCell();
                    nextCell.setActor(this);
                    cell = nextCell;
                }
            }
        }
    }
}
