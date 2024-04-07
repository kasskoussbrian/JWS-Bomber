package fr.epita.assistants.jws.utils;

import fr.epita.assistants.jws.data.model.PlayerModel;

public class playerUtils {
   public Integer id;
   public String name;

   public Integer lives;
   public Integer posX;
   public Integer posY;

    public playerUtils(String name, Integer posX, Integer posY,int id,int lives) {
        this.name = name;
        this.lives = lives;
        this.posX = posX;
        this.posY = posY;
        this.id = id;
    }
}
