package HotelManagement.recipe.costPerRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.bytecode.enhance.spi.interceptor.AbstractLazyLoadInterceptor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CostPerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String requestNumber;
    private String recipeNumber;
    private String foodStockNumber;
    private String stockName;
    private Double foodStockQuantity;
    private String spiceNumber;
    private Double foodStockPrice;
}
