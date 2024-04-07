package fr.epita.assistants.data.model;

import io.vertx.codegen.ClassModel;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name ="student_model")
public class StudentModel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long id;
    public @Column(name = "name")  String name;
    public @Column(name = "course_id") Long course_id;



}
