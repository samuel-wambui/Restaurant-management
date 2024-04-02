package FINALEQUIFARM.EQUIFARM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="roles")
@Setter
@Getter
public class Role {
    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;


    public static List<String> findByName(List<String> roleNames) {
        return roleNames;
    }
}
