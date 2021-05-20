package com.codecool.dungeoncrawl.json;

import com.codecool.dungeoncrawl.logic.actors.Actor;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JSON {
    public static JSONObject writeToJsonFile(String filename, List data) throws IOException {
        //Player
        JSONObject main = new JSONObject();
        JSONObject player = new JSONObject();
        player.put("id", data.get(0).toString());
        player.put("player", data.get(1));
        player.put("healt", data.get(2));
        player.put("damage", data.get(3));
        JSONArray position = new JSONArray();
        //Position
        position.addAll((Collection) data.get(4));
        player.put("position", position);
        JSONArray items = new JSONArray();
        // Inventory just names
        items.addAll((Collection) data.get(5));
        player.put("items", items);
        // Enemies
        JSONObject enemies = new JSONObject();
        List<AiActor> enemiesFromData = (List<AiActor>) data.get(6);
        enemiesFromData.stream().peek(enemy -> {
            Arrays.asList(enemy.getTileName(), enemy.getHealth(), enemy.getDamage(),
                    Arrays.asList(enemy.getX(), enemy.getY()));
        }).collect(Collectors.toList());
        enemies.put("enemies", enemiesFromData);
        //All JSON object into main
        main.put("player", player);
        main.put("enemies", enemies);
        Files.write(Paths.get(filename+".json"), main.toJSONString().getBytes());
        return player;
    }

    public static Object readJson(String filename) throws IOException, ParseException {
        FileReader fileReader = new FileReader(filename+".json");
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(fileReader);
    }
}
