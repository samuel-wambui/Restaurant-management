package HotelManagement.billing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    // Query to get total billing amount for a specific customer
    @Query("SELECT SUM(b.totalAmount) FROM Billing b WHERE b.customer.id = :customerId")
    Double getTotalAmountByCustomerId(Long customerId);

    // Query to get total billing amount for a specific period
    @Query("SELECT SUM(b.totalAmount) FROM Billing b WHERE b.billingDate BETWEEN :startDate AND :endDate")
    Double getTotalAmountByDateRange(Date startDate, Date endDate);

    // Query to get all billings for a specific customer
    @Query("SELECT b FROM Billing b WHERE b.customer.id = :customerId")
    List<Billing> getAllBillingsByCustomerId(Long customerId);
}
