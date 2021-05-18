package com.codecool.dungeoncrawl.dao;

import com.codecool.dungeoncrawl.model.AiActorModel;

import java.util.List;

public interface AiActorDao {
    void add(AiActorModel enemy);
    void update(AiActorModel enemy);
    AiActorModel get(int id);
    List<AiActorModel> getAll();
}
