package fr.epita.assistants.jws.data.model;

import org.hibernate.annotations.Target;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "game")

public class GameModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public @Column(name ="starttime") Timestamp starttime;
    public @Column(name="state") String state;
   public @Column(name ="map")  @ElementCollection(targetClass = String.class) List<String> map;

}
