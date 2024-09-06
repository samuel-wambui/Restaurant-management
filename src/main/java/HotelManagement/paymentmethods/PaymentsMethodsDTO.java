package HotelManagement.paymentmethods;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class PaymentsMethodsDTO {
    private String methodName;
    private String dateCreated;
    private boolean isActive;
}
