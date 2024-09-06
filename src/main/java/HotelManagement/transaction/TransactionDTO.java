package HotelManagement.transaction;

import lombok.Data;

@Data
public class TransactionDTO {
    private String transactionCode;
    private String transactionNumber;
    private Double transactionAmount;
    private Long bookingId;
    private Long paymentMethodId;
    private String transactionMode;
    private String resultCode;
    private String resultDesc;
    private String status;
    private String datePaid;
}
