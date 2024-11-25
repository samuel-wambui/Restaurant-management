package HotelManagement.foodStock;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class FoodStockDetailsDTO {
    private Long id;
    private String stockName;
    private String stockNumber;
    private String unitNumber;
    private Double quantityInUnits;
    private String unitName;
    private Double quantityInSubUnits;
    private String subUnitName;
    private Double unitPrice;
    private Double totalCost;
    private Double discount;
    private Date expiryDate;

    // Constructor for mapping projection
    public FoodStockDetailsDTO(Long id,String stockName,String stockNumber,String unitNumber,
                               Double quantityInUnits,String unitName, Double quantityInSubUnits,String subUnitName,
                               Double unitPrice,Double totalCost,Double discount,Date expiryDate ) {
        this.id = id;
        this.stockName = stockName;
        this.stockNumber = stockNumber;
        this.unitNumber = unitNumber;
        this.quantityInUnits = quantityInUnits;
        this.unitName= unitName;
        this.quantityInSubUnits = quantityInSubUnits;
        this.subUnitName= subUnitName;
        this.unitPrice = unitPrice;
        this.totalCost = totalCost;
        this.discount = discount;
        this.expiryDate = expiryDate;
    }

    public static FoodStockDetailsDTO fromProjection(FoodStockProjection projection) {
        return new FoodStockDetailsDTO(
                projection.getId(),
                projection.getStockName(),
                projection.getStockNumber(),
                projection.getUnitNumber(),
                projection.getQuantityInUnits(),
                projection.getUnitName(),
                projection.getQuantityInSubUnits(),
                projection.getSubUnitName(),
                projection.getUnitPrice(),
                projection.getTotalCost(),
                projection.getDiscount(),
                projection.getExpiryDate()
        );
    }

    public static List<FoodStockDetailsDTO> fromProjections(List<FoodStockProjection> projections) {

        return projections.stream()
                .map(FoodStockDetailsDTO::fromProjection)
                .collect(Collectors.toList());
    }

}
