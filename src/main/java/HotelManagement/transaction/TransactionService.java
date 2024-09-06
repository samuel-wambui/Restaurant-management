package HotelManagement.transaction;

import HotelManagement.paymentmethods.PaymentsMethods;
import HotelManagement.paymentmethods.PaymentsMethodsRepository;
import HotelManagement.rooms.booking.Booking;
import HotelManagement.rooms.booking.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final PaymentsMethodsRepository paymentsMethodsRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              BookingRepository bookingRepository,
                              PaymentsMethodsRepository paymentsMethodsRepository) {
        this.transactionRepository = transactionRepository;
        this.bookingRepository = bookingRepository;
        this.paymentsMethodsRepository = paymentsMethodsRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction createTransaction(Transaction transaction) {
        validateTransaction(transaction);
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction transaction) {
        if (transactionRepository.existsById(id)) {
            validateTransaction(transaction);
            transaction.setId(id);
            return transactionRepository.save(transaction);
        } else {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
    }

    public void deleteTransaction(Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
        } else {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
    }

    private void validateTransaction(Transaction transaction) {
        // Ensure the related Booking exists
        Optional<Booking> booking = bookingRepository.findById(transaction.getBooking().getId());
        if (!booking.isPresent()) {
            throw new RuntimeException("Booking not found with ID: " + transaction.getBooking().getId());
        }

        // Ensure the related Payment Method exists
        Optional<PaymentsMethods> paymentMethod = paymentsMethodsRepository.findById(transaction.getPaymentMethod().getId());
        if (!paymentMethod.isPresent()) {
            throw new RuntimeException("Payment method not found with ID: " + transaction.getPaymentMethod().getId());
        }
    }
}
