package HotelManagement.billing;

import HotelManagement.customer.Customer;
import HotelManagement.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    public Billing getBillingById(Long id) {
        return billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing not found"));
    }

    public Billing createBilling(BillingDto billingDto) {
        Customer customer = customerRepository.findById(billingDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Billing billing = new Billing();
        billing.setCustomer(customer);
        billing.setTotalAmount(billingDto.getTotalAmount());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date billingDate = dateFormat.parse(billingDto.getBillingDate());
            billing.setBillingDate(billingDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format");
        }

        billing.setPaymentMethod(billingDto.getPaymentMethod());
        return billingRepository.save(billing);
    }

    public Billing updateBilling(Long id, BillingDto billingDto) {
        Billing existingBilling = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing not found"));

        Customer customer = customerRepository.findById(billingDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        existingBilling.setCustomer(customer);
        existingBilling.setTotalAmount(billingDto.getTotalAmount());

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date billingDate = dateFormat.parse(billingDto.getBillingDate());
            existingBilling.setBillingDate(billingDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format");
        }

        existingBilling.setPaymentMethod(billingDto.getPaymentMethod());
        return billingRepository.save(existingBilling);
    }

    public void deleteBilling(Long id) {
        if (!billingRepository.existsById(id)) {
            throw new RuntimeException("Billing not found");
        }
        billingRepository.deleteById(id);
    }

    public Double getTotalAmountByCustomerId(Long customerId) {
        return billingRepository.getTotalAmountByCustomerId(customerId);
    }

    public Double getTotalAmountByDateRange(String startDateStr, String endDateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            return billingRepository.getTotalAmountByDateRange(startDate, endDate);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format");
        }
    }

    public List<Billing> getAllBillingsByCustomerId(Long customerId) {
        return billingRepository.getAllBillingsByCustomerId(customerId);
    }
}
