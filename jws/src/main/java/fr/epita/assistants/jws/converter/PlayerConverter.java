package fr.epita.assistants.jws.converter;

import fr.epita.assistants.jws.data.model.PlayerModel;
import fr.epita.assistants.jws.domain.entity.PlayerEntity;

public class PlayerConverter {
    public static PlayerEntity convertModelToEntity(PlayerModel playerModel)
    {
        return new PlayerEntity(playerModel.id,playerModel.name, playerModel.lives, playerModel.posX, playerModel.posY, playerModel.lastbomb,playerModel.lastmovement,playerModel.position, playerModel.game_id);
    }

}
