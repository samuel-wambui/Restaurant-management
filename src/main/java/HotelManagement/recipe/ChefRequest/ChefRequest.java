package HotelManagement.recipe.ChefRequest;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChefRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String chefRequestNumber;
    private String recipeNumber;
    private String missingClause;
    @JsonFormat(pattern = "dd/MM/yyyy 'Time:' HH:mm:ss")
    private LocalDateTime dateTime;
}
