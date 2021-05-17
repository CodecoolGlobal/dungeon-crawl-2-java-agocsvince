package com.codecool.dungeoncrawl.logic.actors.ai;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.actors.Actor;


public abstract class AiActor extends Actor {
    protected String name;

    public AiActor(Cell cell) {
        super(cell);
    }

    public abstract void makeMove();


    @Override
    public String getTileName() { return name; }

    public void die() {
        isAlive = false;
        GameEngine.soundEngine.play(name);
    }

    /*
    Enemy types:
    (P)Spider: has a web on the map, when the player enters the web he gets stuck and the spider moves to the player
    (p)Cobweb: (celltype for spider) spider scans its web
    (z)Zombie: there are graves on the map, when the player gets close to them a zombie pops out
    (g)Golem: moves around randomly on the map
    (s)Skeleton: basic static enemy, doesn't move
    (t)Wraith: picks a random position on the map, then goes towards it (can pass walls). When it gets there this repeats
    (m)Mage: heals enemies around itself, doesnt move
    (M)Mimic: finds nearest item and sits on it

     */
}
