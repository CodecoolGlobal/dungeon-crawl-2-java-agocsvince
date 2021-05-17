package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.actors.ai.*;
import com.codecool.dungeoncrawl.logic.items.*;
import com.codecool.dungeoncrawl.logic.actors.Player;


import java.io.InputStream;
import java.util.Scanner;

public class MapLoader {
    public static GameMap loadMap() {
        InputStream streamFromTxt = MapLoader.class.getResourceAsStream("/map.txt");
        Scanner scanner = new Scanner(streamFromTxt);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        scanner.nextLine(); // empty line

        GameMap map = new GameMap(width, height, CellType.EMPTY);
        for (int y = 0; y < height; y++) {
            String line = scanner.nextLine();
            for (int x = 0; x < width; x++) {
                if (x < line.length()) {
                    Cell cell = map.getCell(x, y);
                    switch (line.charAt(x)) {
                        case ' ':
                            cell.setType(CellType.EMPTY);
                            break;
                        case '#':
                            cell.setType(CellType.WALL);
                            break;
                        case '.':
                            cell.setType(CellType.FLOOR);
                            break;
                        case 's':
                            cell.setType(CellType.FLOOR);
                            new Skeleton(cell);
                            break;
                        case 'g':
                            cell.setType(CellType.FLOOR);
                            GameEngine.aiList.add(new Golem(cell));
                            break;
                        case '@':
                            cell.setType(CellType.FLOOR);
                            map.setPlayer(new Player(cell));
                            break;
                        case 'k':
                            cell.setType(CellType.FLOOR);
                            new Key(cell);
                            break;
                        case 'w':
                            cell.setType(CellType.FLOOR);
                            new Sword(cell);
                            break;
                        case 'd':
                            cell.setType(CellType.FLOOR);
                            new Door(cell);
                            break;
                        case 'W':
                            GameEngine.aiList.add(new Wraith(cell, map));
                            break;
                        case 'z':
                            cell.setType(CellType.FLOOR);
                            GameEngine.aiList.add(new Zombie(cell, map));
                            break;
                        default:
                            throw new RuntimeException("Unrecognized character: '" + line.charAt(x) + "'");
                    }
                }
            }
        }
        return map;
    }

}
