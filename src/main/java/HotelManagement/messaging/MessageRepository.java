package HotelManagement.messaging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    // Find the message with the highest ID (latest message)
    @Query("SELECT m FROM MessageEntity m ORDER BY m.id DESC")
    Optional<MessageEntity> findTopByOrderByIdDesc();
}
