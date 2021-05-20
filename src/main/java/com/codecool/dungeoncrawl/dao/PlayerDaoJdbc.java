package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.GameEngine;
import com.codecool.dungeoncrawl.logic.items.Item;
import com.codecool.dungeoncrawl.model.PlayerModel;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDaoJdbc implements PlayerDao {
    private DataSource dataSource;

    public PlayerDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(PlayerModel player) {
        try (Connection conn = dataSource.getConnection()) {
            String sql =
                    "INSERT INTO player (id, player_name, hp, x, y, purse, item_hand_r, item_hand_l, item_head, item_chest) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, player.getUuid());
            statement.setString(2, player.getPlayerName());
            statement.setInt(3, player.getHp());
            statement.setInt(4, player.getX());
            statement.setInt(5, player.getY());

            ArrayList<Item> purse = GameEngine.getPlayer().getInventory();
            String purseContents = "";
            for (Item item : purse) {
                purseContents += item.getItemID() + " ";
            }
            statement.setString(6, purseContents); //purse

            int rightHandId = -1;
            int leftHandId = -1;
            int headId = -1;
            int torsoId = -1;

            if (GameEngine.getPlayer().getrHandSlot() != null)
                rightHandId = GameEngine.getPlayer().getrHandSlot().getItemID();
            if (GameEngine.getPlayer().getlHandSlot() != null)
                leftHandId = GameEngine.getPlayer().getlHandSlot().getItemID();
            if (GameEngine.getPlayer().getHeadSlot() != null)
                headId = GameEngine.getPlayer().getHeadSlot().getItemID();
            if (GameEngine.getPlayer().getTorsoSlot() != null)
                torsoId = GameEngine.getPlayer().getTorsoSlot().getItemID();

            statement.setInt(7, rightHandId);
            statement.setInt(8, leftHandId);
            statement.setInt(9, headId);
            statement.setInt(10, torsoId);
            statement.executeUpdate();
//            ResultSet resultSet = statement.getGeneratedKeys();
//            resultSet.next();
//            player.setId(resultSet.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(PlayerModel player) {
    }

    @Override
    public PlayerModel get(int id) {
        return null;
    }

    @Override
    public List<PlayerModel> getAll() {
        return null;
    }
}
