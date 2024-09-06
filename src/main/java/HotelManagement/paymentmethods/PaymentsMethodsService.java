package HotelManagement.paymentmethods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentsMethodsService {

    private final PaymentsMethodsRepository paymentsMethodsRepository;

    @Autowired
    public PaymentsMethodsService(PaymentsMethodsRepository paymentsMethodsRepository) {
        this.paymentsMethodsRepository = paymentsMethodsRepository;
    }

    public List<PaymentsMethods> getAllPaymentMethods() {
        return paymentsMethodsRepository.findAll();
    }

    public Optional<PaymentsMethods> getPaymentMethodById(Long id) {
        return paymentsMethodsRepository.findById(id);
    }

    public PaymentsMethods createPaymentMethod(PaymentsMethods paymentMethod) {
        return paymentsMethodsRepository.save(paymentMethod);
    }

    public PaymentsMethods updatePaymentMethod(Long id, PaymentsMethods paymentMethod) {
        if (paymentsMethodsRepository.existsById(id)) {
            paymentMethod.setId(id);
            return paymentsMethodsRepository.save(paymentMethod);
        } else {
            throw new RuntimeException("Payment method not found with ID: " + id);
        }
    }

    public void deletePaymentMethod(Long id) {
        if (paymentsMethodsRepository.existsById(id)) {
            paymentsMethodsRepository.deleteById(id);
        } else {
            throw new RuntimeException("Payment method not found with ID: " + id);
        }
    }
}
