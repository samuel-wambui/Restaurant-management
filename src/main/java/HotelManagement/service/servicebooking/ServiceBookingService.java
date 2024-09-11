package HotelManagement.service.servicebooking;

import HotelManagement.customer.Customer;
import HotelManagement.customer.CustomerRepository;
import HotelManagement.service.Services;  // Ensure this matches your renamed Service class
import HotelManagement.service.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ServiceBookingService {

    @Autowired
    private ServiceBookingRepository serviceBookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceBooking> getAllBookings() {
        return serviceBookingRepository.findAll();
    }

    public ServiceBooking getBookingById(Long id) {
        return serviceBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public ServiceBooking createBooking(ServiceBookingDto bookingDto) {
        Customer customer = customerRepository.findById(bookingDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Services service = serviceRepository.findById(bookingDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(customer);
        booking.setService(service);  // Corrected from services to service

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date bookingDate = dateFormat.parse(bookingDto.getBookingDate());
            booking.setBookingDate(bookingDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format");
        }

        booking.setTimeSlot(bookingDto.getTimeSlot());
        return serviceBookingRepository.save(booking);
    }

    public ServiceBooking updateBooking(Long id, ServiceBookingDto bookingDto) {
        ServiceBooking existingBooking = serviceBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        Customer customer = customerRepository.findById(bookingDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Services service = serviceRepository.findById(bookingDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        existingBooking.setCustomer(customer);
        existingBooking.setService(service);  // Corrected from services to service

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date bookingDate = dateFormat.parse(bookingDto.getBookingDate());
            existingBooking.setBookingDate(bookingDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format");
        }

        existingBooking.setTimeSlot(bookingDto.getTimeSlot());
        return serviceBookingRepository.save(existingBooking);
    }

    public void deleteBooking(Long id) {
        if (!serviceBookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found");
        }
        serviceBookingRepository.deleteById(id);
    }
}
