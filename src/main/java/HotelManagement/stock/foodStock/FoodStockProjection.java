package HotelManagement.stock.foodStock;

import java.util.Date;

public interface FoodStockProjection {
    Long getId();
    String getStockName();
    String getStockNumber();
    String getUnitNumber();
    String getUnitName();
    Double getQuantityInUnits();
    String getSubUnitName();
    Double getQuantityInSubUnits();
    Double getTotalCost();
    Double getUnitPrice();
    Double getDiscount();
    Date getExpiryDate();
}

