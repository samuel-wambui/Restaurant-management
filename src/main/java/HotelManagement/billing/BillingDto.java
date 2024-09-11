package HotelManagement.billing;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
@Data
public class BillingDto {
    private Long customerId;
    private Double totalAmount;
    private String billingDate;  // Use String for date input in DTO
    private String paymentMethod;
}
