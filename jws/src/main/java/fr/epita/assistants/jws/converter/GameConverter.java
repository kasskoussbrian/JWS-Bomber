package fr.epita.assistants.jws.converter;

import fr.epita.assistants.jws.data.model.GameModel;
import fr.epita.assistants.jws.domain.entity.GameEntity;
import fr.epita.assistants.jws.utils.State;

import java.util.ArrayList;
import java.util.List;

public class GameConverter {
    public static GameEntity convertModelToEntity(GameModel gameModel)
    {
        State state = State.STARTING;
        if (gameModel.state.equals("STARTING"))
        {
            state = State.STARTING;
        }
        else if(gameModel.state.equals("FINISHED"))
        {
            state = State.FINISHED;
        }
        else if(gameModel.state .equals("RUNNING"))
        {
            state = State.RUNNING;
        }
        else
        {
            System.err.println("Unkown token given to convertor: "+gameModel.state);
        }
        return new GameEntity(gameModel.starttime,state,gameModel.map,gameModel.id);
    }
    public static List<GameEntity> convertModelToEntityList(List<GameModel> gameModelList)
    {
        List<GameEntity> toRet = new ArrayList<>();
        for (GameModel e : gameModelList)
        {
            toRet.add(convertModelToEntity(e));
        }
        return toRet;
    }


}
