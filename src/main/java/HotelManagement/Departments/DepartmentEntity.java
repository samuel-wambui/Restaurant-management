package HotelManagement.Departments;

import HotelManagement.Auth.user.User;
import HotelManagement.stock.Category.Category;
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
public class DepartmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String departmentName;
    private String departmentCode;
    private boolean isDeleted = false;

    @ManyToMany(mappedBy = "departments")
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "departments")
    private Set<User> users = new HashSet<>();
}
