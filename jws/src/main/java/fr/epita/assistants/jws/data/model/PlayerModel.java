package fr.epita.assistants.jws.data.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "player")
public class PlayerModel {

   public @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    public @Column(name="lastbomb") Timestamp lastbomb;
    public @Column(name="lastmovement") Timestamp lastmovement;
    public @Column(name="lives") Integer lives;
    public @Column(name="name") String name;
    public @Column(name="posX") Integer posX;
    public @Column(name="posY") Integer posY;
    public @Column(name="position") Integer position;

    public @Column(name="game_id") Long game_id;
}
