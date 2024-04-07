package fr.epita.assistants.jws.domain.entity;

import java.sql.Timestamp;

public class PlayerEntity {
    public Long id;
    public String name;
    public int  lives;
    public int posX;
    public int posY;

    public Timestamp lastBomb;
    public Timestamp lastMovement;
    public Integer position;

    public Long game_id;

    public PlayerEntity(Long id, String name, int lives, int posX, int posY, Timestamp lastBomb, Timestamp lastMovement, Integer position, Long game_id) {
        this.id = id;
        this.name = name;
        this.lives = lives;
        this.posX = posX;
        this.posY = posY;
        this.lastBomb = lastBomb;
        this.lastMovement = lastMovement;
        this.position = position;
        this.game_id = game_id;
    }
}
