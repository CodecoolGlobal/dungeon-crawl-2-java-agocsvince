package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.model.AiActorModel;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class AiActorDaoJdbc implements AiActorDao {
    private DataSource dataSource;

    public AiActorDaoJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(AiActorModel enemy, UUID playerId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "INSERT INTO enemy (enemy_type, hp, x, y, player_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, enemy.getType());
            statement.setInt(2, enemy.getHp());
            statement.setInt(3, enemy.getX());
            statement.setInt(4, enemy.getY());
            statement.setString(5, playerId.toString());
            statement.executeUpdate();
//            ResultSet resultSet = statement.getGeneratedKeys();
//            resultSet.next();
//            enemy.setId(resultSet.getInt(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(AiActorModel player) {

    }

    @Override
    public AiActorModel get(int id) {
        return null;
    }

    @Override
    public List<AiActorModel> getAll() {
        return null;
    }
}
