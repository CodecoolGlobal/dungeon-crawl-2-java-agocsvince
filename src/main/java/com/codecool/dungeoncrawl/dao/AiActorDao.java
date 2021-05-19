package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.model.AiActorModel;

import java.util.List;
import java.util.UUID;

public interface AiActorDao {
    void add(AiActorModel enemy, UUID playerId);
    void update(AiActorModel enemy);
    AiActorModel get(int id);
    List<AiActorModel> getAll();
}
