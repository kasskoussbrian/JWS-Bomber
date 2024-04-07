package fr.epita.assistants.jws.domain.entity;

import fr.epita.assistants.jws.utils.State;

import java.sql.Timestamp;
import java.util.List;

public class GameEntity {
    public Long id;

    public Timestamp startTime;

    public State state;

    public List<String> Map;


    public GameEntity(Timestamp startTime,State state, List<String> map , Long id) {
        this.startTime = startTime;
        this.state = state;
        Map = map;
        this.id = id;
    }
}
