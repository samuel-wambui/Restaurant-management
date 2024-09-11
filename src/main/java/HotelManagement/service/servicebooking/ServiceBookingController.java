package HotelManagement.service.servicebooking;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/service-bookings")
public class ServiceBookingController {

    @Autowired
    private ServiceBookingService serviceBookingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceBooking>>> getAllBookings() {
        try {
            List<ServiceBooking> bookings = serviceBookingService.getAllBookings();
            return ResponseEntity.ok(new ApiResponse<>("Bookings retrieved successfully", 200, bookings));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve bookings", 500, null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceBooking>> getBookingById(@PathVariable Long id) {
        try {
            ServiceBooking booking = serviceBookingService.getBookingById(id);
            return ResponseEntity.ok(new ApiResponse<>("Booking retrieved successfully", 200, booking));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to retrieve booking", 500, null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceBooking>> createBooking(@RequestBody ServiceBookingDto bookingDto) {
        try {
            ServiceBooking newBooking = serviceBookingService.createBooking(bookingDto);
            return ResponseEntity.status(201)
                    .body(new ApiResponse<>("Booking created successfully", 201, newBooking));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to create booking", 500, null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceBooking>> updateBooking(@PathVariable Long id, @RequestBody ServiceBookingDto bookingDto) {
        try {
            ServiceBooking updatedBooking = serviceBookingService.updateBooking(id, bookingDto);
            return ResponseEntity.ok(new ApiResponse<>("Booking updated successfully", 200, updatedBooking));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to update booking", 500, null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Long id) {
        try {
            serviceBookingService.deleteBooking(id);
            return ResponseEntity.ok(new ApiResponse<>("Booking deleted successfully", 200, null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResponse<>(e.getMessage(), 404, null));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>("Failed to delete booking", 500, null));
        }
    }
}
