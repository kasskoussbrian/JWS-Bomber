package fr.epita.assistants.jws.presentation.rest.response;

import fr.epita.assistants.jws.domain.entity.PlayerEntity;
import fr.epita.assistants.jws.utils.State;
import fr.epita.assistants.jws.utils.playerUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GameDetailResponse {
    public Timestamp startTime;

    public State state;


    public List<playerUtils> players;

    public List<String> map;
    public Integer id;

    public GameDetailResponse(List<playerUtils> players, List<String> map, Integer id,Timestamp timestamp,State state) {
        this.id = id;
        this.startTime = timestamp;
        this.players = players;
        this.map = map;
        this.state = state;

    }
}
