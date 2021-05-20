package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.Cell;
import com.codecool.dungeoncrawl.logic.CellType;
import com.codecool.dungeoncrawl.logic.GameMap;
import com.codecool.dungeoncrawl.logic.MapLoader;
import com.codecool.dungeoncrawl.logic.actors.Player;
import com.codecool.dungeoncrawl.logic.actors.ai.AiActor;
import com.codecool.dungeoncrawl.model.AiActorModel;
import com.codecool.dungeoncrawl.model.GameState;
import com.codecool.dungeoncrawl.model.PlayerModel;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class GameDatabaseManager {
    private DataSource dataSource;
    private PlayerDao playerDao;
    private AiActorDao enemiesDao;
    private GameStateDao gameStateDao;

    public void setup() throws SQLException {
        dataSource = connect();
        playerDao = new PlayerDaoJdbc(dataSource);
        enemiesDao = new AiActorDaoJdbc(dataSource);
        gameStateDao = new GameStateDaoJdbc(dataSource);

    }

    public void savePlayer(Player player) {
        PlayerModel model = new PlayerModel(player);
        playerDao.add(model);

    }

    private DataSource connect() throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        String dbName = System.getenv("PSQL_DB_NAME");
        String user = System.getenv("PSQL_USER_NAME");
        String password = System.getenv("PSQL_PASSWORD");

        dataSource.setDatabaseName(dbName);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        System.out.println("Trying to connect");
        dataSource.getConnection().close();
        System.out.println("Connection ok.");

        return dataSource;
    }

    public void saveEnemies(ArrayList<AiActor> aiList, UUID uuid) {
        for (AiActor enemy : aiList) {
            AiActorModel model = new AiActorModel(enemy);
            enemiesDao.add(model, uuid);
        }
    }

    public void saveGameState(String currentMap, Date savedAt, PlayerModel playerModel) {
        GameState model = new GameState(currentMap, savedAt, playerModel);
        gameStateDao.add(model);
    }

    public Player loadPlayerFromDataBase(Player player) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM player WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, player.getUuid().toString());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            String name = resultSet.getString(2);
            String hp = resultSet.getString(3);
            int x = resultSet.getInt(4);
            int y = resultSet.getInt(5);

            GameMap map = MapLoader.loadMap(0); //magic number, 0: level 1
            Cell loadedCell = new Cell(map, x, y, CellType.FLOOR);
            Player loadedPlayer = new Player(loadedCell);
            System.out.println("Player loaded:");
            System.out.println(loadedPlayer.toString());

            return loadedPlayer;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
