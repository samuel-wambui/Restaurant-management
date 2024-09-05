package HotelManagement.foodorder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByCustomerName(String customerName);

    Optional<FoodOrder> findFirstByCustomerNameOrderByOrderTimeDesc(String customerName);  // Example custom method

    List<FoodOrder> findByStatus(OrderStatus status);  // Find orders by status

    Page<FoodOrder> findByCustomerName(String customerName, Pageable pageable);  // Pagination support
    @Query("SELECT o.totalPrice FROM FoodOrder o WHERE o.id = :id")
    Double findTotalPriceById(@Param("id") Long id);

    @Query("SELECT SUM(o.totalPrice) FROM FoodOrder o")
    Double findTotalPriceOfAllOrders();
}
