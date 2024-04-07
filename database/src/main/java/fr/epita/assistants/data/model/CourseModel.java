package fr.epita.assistants.data.model;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

import javax.persistence.*;
import java.security.PublicKey;
import java.util.List;

@Entity
@Table(name = "course_model")
public class CourseModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    @CollectionTable(name= "course_model_tags",joinColumns = @JoinColumn(name = "course_id"))
    public @Column(name = "name")  String name;
    public @ElementCollection @CollectionTable(name = "course_model_tags",joinColumns = @JoinColumn(name = "course_id")) List<String> tag;
}
