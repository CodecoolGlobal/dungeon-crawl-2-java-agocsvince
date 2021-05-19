package com.codecool.dungeoncrawl.model;

import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;

public class AiActorModel extends BaseModel{
    private String type;
    private int hp;
    private int x;
    private int y;

    public AiActorModel(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public AiActorModel(AiActor actor) {
        this.type = actor.getTileName();
        this.x = actor.getX();
        this.y = actor.getY();

        this.hp = actor.getHealth();

    }

    public String getType() {
        return type;
    }

    public void setPlayerName(String playerName) {
        this.type = playerName;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
