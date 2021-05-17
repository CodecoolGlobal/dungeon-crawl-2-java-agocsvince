package com.codecool.dungeoncrawl.logic.actors.ai;

import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.actors.Actor;
import com.codecool.dungeoncrawl.logic.actors.Player;

public class Wraith extends AiActor {

    GameMap map;
    Cell destination;
    Cell startPosition;
    AiActor steppedOnActor = null;

    public Wraith(Cell cell, GameMap map) {
        super(cell);
        this.map = map;
        this.destination = pickNewDestination();
        this.damage = 100;
        this.startPosition = cell;
        name = "wraith";
    }


    @Override
    public void takeDamage(int damage) {
    }

    @Override
    public void die() {
    }

    @Override
    public void makeMove() {





        //if wraith reaches destination
        if (this.cell.getX() == this.destination.getX() && this.cell.getY() == this.destination.getY()) {
            pickNewDestination();
        }

        //get direction
        int dx = 0;
        int dy = 0;
        if (cell.getX() < destination.getX()) {
            dx = 1;
        }
        if (cell.getX() > destination.getX()) {
            dx = -1;
        }
        if (cell.getY() < destination.getY()) {
            dy = 1;
        }
        if (cell.getY() > destination.getY()) {
            dy = -1;
        }


        //move
        Cell nextCell = cell.getNeighbor(dx, dy);
        Actor nbActor = nextCell.getActor();

        //wraith meets player, player dies
        if (nbActor instanceof Player) {
            attack(nbActor, damage);
        }
        //Potential duplication bug if the AI under the wraith moves at the same moment as the wraith
        else if (nbActor == null) {
            if (steppedOnActor != null) {
                cell.setActor(steppedOnActor);
                steppedOnActor = null;
            } else {
                cell.setActor(null);
            }
            nextCell.setActor(this);
            cell = nextCell;
        }
        else {
            steppedOnActor = (AiActor) nbActor;
            cell = nextCell;
        }
    }

    public Cell pickNewDestination() {
        int newDestinationX = (int) (Math.random() * map.getWidth());
        int newDestinationY = (int) (Math.random() * map.getHeight());

        Cell newDestination = map.getCell(newDestinationX, newDestinationY);
        //System.out.println("picking new destination: " + newDestination.getX() + "-" + newDestination.getY());
        this.destination = newDestination;
        return newDestination;
    }
}
