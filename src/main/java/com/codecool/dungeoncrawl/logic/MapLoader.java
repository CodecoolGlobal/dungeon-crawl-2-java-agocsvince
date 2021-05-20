package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.actors.ai.*;
import com.codecool.dungeoncrawl.logic.items.*;
import com.codecool.dungeoncrawl.logic.actors.Player;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class MapLoader {

    private static ArrayList<String> maps = new ArrayList<>();
    private static int currentMapIndex = 0;


    static {
        maps.add("map1");
    }

    private static GameMap activeMap;

    public static GameMap getActiveMap() {
        return activeMap;
    }

    public static GameMap loadNextMap() {
        activeMap = loadMap(currentMapIndex+1);
        return activeMap;
    }

    public static GameMap loadMap(int mapIndex) {

        InputStream streamFromTxt = MapLoader.class.getResourceAsStream("/"+maps.get(mapIndex)+".txt");
        System.out.println(streamFromTxt);
        Scanner scanner = new Scanner(streamFromTxt);
        int width = scanner.nextInt();
        int height = scanner.nextInt();
        currentMapIndex = mapIndex;
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
                            //new Skeleton(cell);
                            GameEngine.aiList.add(new Skeleton(cell));
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
        activeMap = map;
        map.printActors();
        return activeMap;
    }

}
