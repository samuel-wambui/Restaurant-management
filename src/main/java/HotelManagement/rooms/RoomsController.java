package HotelManagement.rooms;

import java.util.List;
import java.util.Optional;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@Validated
public class RoomsController {

    private final RoomsService roomsService;

    @Autowired
    public RoomsController(RoomsService roomsService) {
        this.roomsService = roomsService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Rooms>>> getAllRooms() {
        try {
            List<Rooms> rooms = roomsService.getAllRooms();
            return ResponseEntity.ok(new ApiResponse<>("Rooms fetched successfully.", HttpStatus.OK.value(), rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to fetch rooms: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Rooms>> getRoomById(@PathVariable Long id) {
        try {
            Optional<Rooms> room = roomsService.getRoomById(id);
            if (room.isPresent()) {
                return ResponseEntity.ok(new ApiResponse<>("Room found.", HttpStatus.OK.value(), room.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>("Room not found with ID: " + id, HttpStatus.NOT_FOUND.value(), null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to fetch room: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Rooms>> createRoom(@RequestBody @Validated Rooms room) {
        try {
            Rooms savedRoom = roomsService.saveRoom(room);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Room created successfully.", HttpStatus.CREATED.value(), savedRoom));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to create room: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        try {
            roomsService.deleteRoom(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>("Room deleted successfully.", HttpStatus.NO_CONTENT.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to delete room: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }
}
