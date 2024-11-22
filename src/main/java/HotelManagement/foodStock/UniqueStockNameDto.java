package HotelManagement.foodStock;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UniqueStockNameDto {
    private String stockName;
    private Long id;
    UniqueStockNameDto(String stockName, Long id){
        this.stockName = stockName;
        this.id= id;
        System.out.println("projections: " + id + " "  +stockName);
    }
    public static UniqueStockNameDto fromProjection(UniqueStockNameProjection projection) {
        return new UniqueStockNameDto(
                projection.getStockName(),
                projection.getId()


        );
    }

    public static List<UniqueStockNameDto> fromProjections(List<UniqueStockNameProjection> projections) {

        return projections.stream()
                .map(UniqueStockNameDto::fromProjection)
                .collect(Collectors.toList());
    }

}
