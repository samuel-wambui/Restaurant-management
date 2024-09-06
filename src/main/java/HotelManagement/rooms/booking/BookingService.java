package HotelManagement.rooms.booking;

import HotelManagement.rooms.Rooms;
import HotelManagement.rooms.RoomsRepository;
import HotelManagement.rooms.RoomsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomsService roomsService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, RoomsService roomsService) {
        this.bookingRepository = bookingRepository;
        this.roomsService = roomsService;
    }

    public List<Booking> getAllBookings() {
        try {
            return bookingRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch bookings", e);
        }
    }

    public Optional<Booking> getBookingById(Long id) {
        try {
            return bookingRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch booking with ID: " + id, e);
        }
    }

    public Booking createBooking(BookingDTO bookingDTO) {
        try {
            Rooms room = roomsService.getRoomById(bookingDTO.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found with ID: " + bookingDTO.getRoomId()));

            if (!room.isAvailable()) {
                throw new RuntimeException("Room is not available for booking");
            }

            // Create a new booking
            Booking booking = new Booking();
            booking.setRoom(room);
            booking.setGuestName(bookingDTO.getGuestName());
            booking.setGuestEmail(bookingDTO.getGuestEmail());
            booking.setGuestPhoneNo(bookingDTO.getGuestPhoneNo());
            booking.setCheckInDate(bookingDTO.getCheckInDate());
            booking.setCheckOutDate(bookingDTO.getCheckOutDate());
            booking.setAmountPaid(bookingDTO.getAmountPaid());
            booking.setPaymentMethod(bookingDTO.getPaymentMethod());

            Booking savedBooking = bookingRepository.save(booking);

            // Update room availability to false after booking
            roomsService.updateRoomAvailability(room.getId(), false);

            return savedBooking;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create booking", e);
        }
    }

    public Booking updateBooking(Long id, BookingDTO bookingDTO) {
        try {
            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

            Rooms room = roomsService.getRoomById(bookingDTO.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found with ID: " + bookingDTO.getRoomId()));

            booking.setRoom(room);
            booking.setGuestName(bookingDTO.getGuestName());
            booking.setGuestEmail(bookingDTO.getGuestEmail());
            booking.setGuestPhoneNo(bookingDTO.getGuestPhoneNo());
            booking.setCheckInDate(bookingDTO.getCheckInDate());
            booking.setCheckOutDate(bookingDTO.getCheckOutDate());
            booking.setAmountPaid(bookingDTO.getAmountPaid());
            booking.setPaymentMethod(bookingDTO.getPaymentMethod());

            return bookingRepository.save(booking);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update booking", e);
        }
    }

    public void deleteBooking(Long id) {
        try {
            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + id));

            // Mark room as available after booking cancellation
            roomsService.updateRoomAvailability(booking.getRoom().getId(), true);

            bookingRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete booking", e);
        }
    }
}
