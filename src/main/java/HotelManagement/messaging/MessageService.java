package HotelManagement.messaging;

import HotelManagement.foodorder.FoodOrder;
import HotelManagement.foodorder.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private OrderService orderService;

    public List<MessageEntity> getAllMessages() {
        return messageRepository.findAll();
    }

    // Always fetch the latest message based on the highest ID
    public MessageEntity getMessageById(Long id) {
        return messageRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("No messages found"));
    }

    // Customer sends message to place an order
    public MessageEntity sendMessage(MessageDto messageDto, Long orderId) {
        MessageEntity message = new MessageEntity();
        message.setSenderName(messageDto.getSenderName());
        message.setSenderEmail(messageDto.getSenderEmail());
        message.setContent(messageDto.getContent());
        message.setSubject(messageDto.getSubject());

        // If orderId is provided, link it to the message
        if (orderId != null) {
            FoodOrder foodOrder = orderService.getOrderById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
            message.setFoodOrder(foodOrder);
        }

        return messageRepository.save(message);
    }

    // Delivery person replies to an order message
    public MessageEntity replyToMessage(Long messageId, String replyContent) {
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        String updatedContent = message.getContent() + "\n\nDelivery Reply: " + replyContent;
        message.setContent(updatedContent);
        message.setRead(true);
        return messageRepository.save(message);
    }

    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new RuntimeException("Message not found");
        }
        messageRepository.deleteById(id);
    }
}
