package HotelManagement.paymentmethods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments-methods")
@Validated
public class PaymentsMethodsController {

    private final PaymentsMethodsService paymentsMethodsService;

    @Autowired
    public PaymentsMethodsController(PaymentsMethodsService paymentsMethodsService) {
        this.paymentsMethodsService = paymentsMethodsService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentsMethods>> getAllPaymentMethods() {
        List<PaymentsMethods> paymentMethods = paymentsMethodsService.getAllPaymentMethods();
        return ResponseEntity.ok(paymentMethods);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentsMethods> getPaymentMethodById(@PathVariable Long id) {
        Optional<PaymentsMethods> paymentMethod = paymentsMethodsService.getPaymentMethodById(id);
        return paymentMethod.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentsMethods> createPaymentMethod(@RequestBody PaymentsMethods paymentMethod) {
        try {
            PaymentsMethods createdPaymentMethod = paymentsMethodsService.createPaymentMethod(paymentMethod);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPaymentMethod);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PaymentsMethods> updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentsMethods paymentMethod) {
        try {
            PaymentsMethods updatedPaymentMethod = paymentsMethodsService.updatePaymentMethod(id, paymentMethod);
            return ResponseEntity.ok(updatedPaymentMethod);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        try {
            paymentsMethodsService.deletePaymentMethod(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
