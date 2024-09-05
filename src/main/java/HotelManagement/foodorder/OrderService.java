package HotelManagement.foodorder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public FoodOrder createOrder(OrderDto orderDto) {
        try {
            // Convert FoodOrderItemDto to FoodOrderItem
            List<FoodOrderItem> orderItems = orderDto.getOrderItems().stream()
                    .map(dto -> new FoodOrderItem(dto.getItemName(), dto.getPrice()))
                    .toList();

            // Calculate total price
            Double totalPrice = orderItems.stream()
                    .mapToDouble(FoodOrderItem::getPrice)
                    .sum();

            FoodOrder order = new FoodOrder();
            order.setCustomerName(orderDto.getCustomerName());
            order.setOrderItems(orderItems);
            order.setTotalPrice(totalPrice); // Set calculated total price
            order.setOrderTime(new Date());
            order.setStatus(OrderStatus.PENDING);

            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to place order: " + e.getMessage(), e);
        }
    }

    public Optional<FoodOrder> getOrderById(Long id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch order with ID: " + id, e);
        }
    }

    public List<FoodOrder> getAllOrders() {
        try {
            return orderRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders: " + e.getMessage(), e);
        }
    }

    public List<FoodOrder> getOrdersByCustomerName(String customerName) {
        try {
            return orderRepository.findByCustomerName(customerName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders by customer name: " + e.getMessage(), e);
        }
    }

    public List<FoodOrder> getOrdersByStatus(OrderStatus status) {
        try {
            return orderRepository.findByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders by status: " + e.getMessage(), e);
        }
    }

    public Page<FoodOrder> getOrdersByCustomerNamePaginated(String customerName, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return orderRepository.findByCustomerName(customerName, pageable);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve orders with pagination: " + e.getMessage(), e);
        }
    }

    public FoodOrder updateOrder(Long id, OrderDto orderDto) {
        try {
            FoodOrder order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

            // Convert FoodOrderItemDto to FoodOrderItem
            List<FoodOrderItem> updatedOrderItems = orderDto.getOrderItems().stream()
                    .map(dto -> new FoodOrderItem(dto.getItemName(), dto.getPrice()))
                    .toList();

            // Update order details
            order.setCustomerName(orderDto.getCustomerName());
            order.setOrderItems(updatedOrderItems);
            order.setTotalPrice(updatedOrderItems.stream().mapToDouble(FoodOrderItem::getPrice).sum());

            return orderRepository.save(order);
        } catch (RuntimeException e) {
            throw new RuntimeException("Order not found: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order: " + e.getMessage(), e);
        }
    }

    public FoodOrder updateOrderStatus(Long id, OrderStatus status) {
        try {
            FoodOrder order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

            order.setStatus(status);
            return orderRepository.save(order);
        } catch (RuntimeException e) {
            throw new RuntimeException("Order not found: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update order status: " + e.getMessage(), e);
        }
    }

    public void deleteOrder(Long id) {
        try {
            FoodOrder order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
            orderRepository.delete(order);
        } catch (RuntimeException e) {
            throw new RuntimeException("Order not found: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete order: " + e.getMessage(), e);
        }
    }
    public Double getTotalPriceById(Long id) {
        try {
            Double totalPrice = orderRepository.findTotalPriceById(id);
            if (totalPrice == null) {
                throw new RuntimeException("Order not found with ID: " + id);
            }
            return totalPrice;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve total price by ID: " + e.getMessage(), e);
        }
    }

    public Double getTotalPriceOfAllOrders() {
        try {
            return orderRepository.findTotalPriceOfAllOrders();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve total price of all orders: " + e.getMessage(), e);
        }
    }
}
