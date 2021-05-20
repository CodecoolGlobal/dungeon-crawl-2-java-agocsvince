package com.codecool.dungeoncrawl.logic;

import com.codecool.dungeoncrawl.json.JSON;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonTest {
    @Test
    public void test_writeToJson() throws IOException {
        JSON JSON = new JSON();
        List data = Arrays.asList("x-10-x", "Name", "20",
                Arrays.asList(0, 1),
                Arrays.asList("Key", "sword"));
        JSON.writeToJsonFile("test", data);

    }
}
