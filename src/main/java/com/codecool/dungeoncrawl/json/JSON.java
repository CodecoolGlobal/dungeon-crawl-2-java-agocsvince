package com.codecool.dungeoncrawl.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

public class JSON {
    public static JSONObject writeToJsonFile(String filename, List data) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", data.get(0).toString());
        jsonObject.put("player", data.get(1));
        jsonObject.put("healt", data.get(2));
        JSONArray position = new JSONArray();
        position.addAll((Collection) data.get(3));
        jsonObject.put("position", position);
        JSONArray items = new JSONArray();
        items.addAll((Collection) data.get(4));
        jsonObject.put("items", items);
        Files.write(Paths.get(filename+".json"), jsonObject.toJSONString().getBytes());
        return jsonObject;
    }

    public static Object readJson(String filename) throws IOException, ParseException {
        filename = "Vince";
        FileReader fileReader = new FileReader(filename+".json");
        JSONParser jsonParser = new JSONParser();
        return jsonParser.parse(fileReader);
    }
}
