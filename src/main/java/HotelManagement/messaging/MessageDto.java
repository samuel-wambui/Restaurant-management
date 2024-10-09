package HotelManagement.messaging;

import lombok.Data;

@Data
public class MessageDto {
    private String senderName;
    private String senderEmail;
    private String content;
    private String subject;

}
