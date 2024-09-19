package HotelManagement.messaging;

import HotelManagement.foodorder.FoodOrder;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderName;
    private String senderEmail;
    private String content;
    private String subject;

    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    // Link messages to food orders (optional)
    @ManyToOne
    @JoinColumn(name = "food_order_id")
    private FoodOrder foodOrder;

    @PrePersist
    protected void onCreate() {
        this.sentAt = new Date();
    }
}
