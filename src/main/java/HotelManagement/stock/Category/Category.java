package HotelManagement.stock.Category;

import HotelManagement.Departments.DepartmentEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String categoryName;
    private boolean isDeleted = false;

    @ManyToMany
    @JoinTable(
            name = "category_department",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<DepartmentEntity> departments = new HashSet<>();
}
