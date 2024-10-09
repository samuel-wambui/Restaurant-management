package HotelManagement.messaging;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<MessageEntity>>> getAllMessages() {
        try {
            List<MessageEntity> messages = messageService.getAllMessages();
            return ResponseEntity.ok(new ApiResponse<>("Messages retrieved successfully", 200, messages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve messages", 500, null));
        }
    }

    @PostMapping("/send/{orderId}")
    public ResponseEntity<ApiResponse<MessageEntity>> sendMessage(@PathVariable Long orderId, @RequestBody MessageDto messageDto) {
        try {
            MessageEntity message = messageService.sendMessage(messageDto, orderId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Message sent successfully", 201, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to send message", 500, null));
        }
    }

    @PostMapping("/reply/{messageId}")
    public ResponseEntity<ApiResponse<MessageEntity>> replyToMessage(
            @PathVariable Long messageId,
            @RequestBody String replyContent) {
        try {
            MessageEntity message = messageService.replyToMessage(messageId, replyContent);
            return ResponseEntity.ok(new ApiResponse<>("Reply sent successfully", 200, message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to send reply", 500, null));
        }
    }
    // New getMessageById method to retrieve a message by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageEntity>> getMessageById(@PathVariable Long id) {
        try {
            MessageEntity message = messageService.getMessageById(id);
            return ResponseEntity.ok(new ApiResponse<>("Message retrieved successfully", 200, message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Message not found", 404, null));
        }
    }
}
