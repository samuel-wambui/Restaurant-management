package HotelManagement.rooms;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomsService {

    private final RoomsRepository roomsRepository;

    @Autowired
    public RoomsService(RoomsRepository roomsRepository) {
        this.roomsRepository = roomsRepository;
    }

    public List<Rooms> getAllRooms() {
        try {
            return roomsRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch rooms", e);
        }
    }

    public Optional<Rooms> getRoomById(Long id) {
        try {
            return roomsRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch room with ID: " + id, e);
        }
    }

    public Rooms saveRoom(Rooms room) {
        try {
            return roomsRepository.save(room);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save room", e);
        }
    }

    public void deleteRoom(Long id) {
        try {
            roomsRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete room with ID: " + id, e);
        }
    }

    // Logic to update room availability when a booking is made
    public void updateRoomAvailability(Long roomId, boolean isAvailable) {
        Optional<Rooms> roomOpt = roomsRepository.findById(roomId);
        if (roomOpt.isPresent()) {
            Rooms room = roomOpt.get();
            room.setAvailable(isAvailable);
            roomsRepository.save(room);
        } else {
            throw new RuntimeException("Room not found with ID: " + roomId);
        }
    }
}
