package com.codecool.dungeoncrawl;

import com.codecool.dungeoncrawl.logic.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class Tiles {
    public static int TILE_WIDTH = 32;

    private static final Image tileset = new Image("/tiles.png", 543 * 2, 543 * 2, true, false);
    private static final Map<String, Tile> tileMap = new HashMap<>();
    public static class Tile {
        public final int x, y, w, h;
        Tile(int i, int j) {
            x = i * (TILE_WIDTH + 2);
            y = j * (TILE_WIDTH + 2);
            w = TILE_WIDTH;
            h = TILE_WIDTH;
        }
    }

    static {
        tileMap.put("empty", new Tile(0, 0));
        tileMap.put("wall", new Tile(10, 17));
        tileMap.put("floor", new Tile(2, 0));
        tileMap.put("closedDoor", new Tile(13, 11));
        tileMap.put("openedDoor", new Tile(12, 11));

        tileMap.put("player", new Tile(27, 0));

        tileMap.put("skeleton", new Tile(29, 6));
        tileMap.put("golem", new Tile(28, 6));
        tileMap.put("spider", new Tile(29, 6));
        tileMap.put("wraith", new Tile(26, 6));
        tileMap.put("zombie", new Tile(24, 3));
        tileMap.put("sleepingZombie", new Tile(0, 14));
        tileMap.put("stairs", new Tile(3, 6));


        tileMap.put("yellowKey", new Tile(16, 23));
        tileMap.put("redKey", new Tile(18, 23));
        tileMap.put("sword", new Tile(0, 30));
        tileMap.put("healthPotion", new Tile(16, 25));

        tileMap.put("lHelmet", new Tile(0, 22));
        tileMap.put("mHelmet", new Tile(2, 22));
        tileMap.put("hHelmet", new Tile(4, 22));
        tileMap.put("lArmor", new Tile(0, 23));
        tileMap.put("mArmor", new Tile(2, 23));
        tileMap.put("hArmor", new Tile(4, 23));
        tileMap.put("knife", new Tile(2, 28));


    }

    public static void drawTile(GraphicsContext context, Drawable d, int x, int y) {
        Tile tile = tileMap.get(d.getTileName());
        context.drawImage(tileset, tile.x, tile.y, tile.w, tile.h,
                x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
    }

    public static void changeDoor() {
        tileMap.put("openedDoor", new Tile(10, 9));
    }
}
