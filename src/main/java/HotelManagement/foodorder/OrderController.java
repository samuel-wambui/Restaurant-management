package HotelManagement.foodorder;

import HotelManagement.ApiResponse.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FoodOrder>> createOrder(@RequestBody OrderDto orderDto) {
        try {
            FoodOrder order = orderService.createOrder(orderDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("Order placed successfully", HttpStatus.CREATED.value(), order));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to place order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodOrder>> getOrderById(@PathVariable Long id) {
        try {
            Optional<FoodOrder> orderOptional = orderService.getOrderById(id);
            return orderOptional.map(order -> ResponseEntity.ok(new ApiResponse<>("Order retrieved successfully", HttpStatus.OK.value(), order)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>("Order not found", HttpStatus.NOT_FOUND.value(), null)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<FoodOrder>>> getAllOrders() {
        try {
            List<FoodOrder> orders = orderService.getAllOrders();
            return ResponseEntity.ok(new ApiResponse<>("Orders retrieved successfully", HttpStatus.OK.value(), orders));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to fetch orders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/customer/{customerName}")
    public ResponseEntity<ApiResponse<List<FoodOrder>>> getOrdersByCustomerName(@PathVariable String customerName) {
        try {
            List<FoodOrder> orders = orderService.getOrdersByCustomerName(customerName);
            return ResponseEntity.ok(new ApiResponse<>("Orders retrieved successfully", HttpStatus.OK.value(), orders));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve orders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<FoodOrder>>> getOrdersByStatus(@PathVariable OrderStatus status) {
        try {
            List<FoodOrder> orders = orderService.getOrdersByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>("Orders retrieved successfully", HttpStatus.OK.value(), orders));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve orders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/customer/{customerName}/paginated")
    public ResponseEntity<ApiResponse<?>> getOrdersByCustomerNamePaginated(@PathVariable String customerName,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FoodOrder> ordersPage = orderService.getOrdersByCustomerNamePaginated(customerName, page, size);
            return ResponseEntity.ok(new ApiResponse<>("Orders retrieved successfully", HttpStatus.OK.value(), ordersPage));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve orders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodOrder>> updateOrder(@PathVariable Long id, @RequestBody OrderDto orderDto) {
        try {
            FoodOrder updatedOrder = orderService.updateOrder(id, orderDto);
            return ResponseEntity.ok(new ApiResponse<>("Order updated successfully", HttpStatus.OK.value(), updatedOrder));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Order not found: " + e.getMessage(), HttpStatus.NOT_FOUND.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FoodOrder>> updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        try {
            FoodOrder updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(new ApiResponse<>("Order status updated successfully", HttpStatus.OK.value(), updatedOrder));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Order not found: " + e.getMessage(), HttpStatus.NOT_FOUND.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to update order status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(new ApiResponse<>("Order deleted successfully", HttpStatus.OK.value(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Order not found: " + e.getMessage(), HttpStatus.NOT_FOUND.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to delete order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/{id}/total-price")
    public ResponseEntity<ApiResponse<Double>> getTotalPriceById(@PathVariable Long id) {
        try {
            Double totalPrice = orderService.getTotalPriceById(id);
            return ResponseEntity.ok(new ApiResponse<>("Total price retrieved successfully", HttpStatus.OK.value(), totalPrice));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("Order not found with ID: " + id, HttpStatus.NOT_FOUND.value(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve total price: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

    @GetMapping("/total-price")
    public ResponseEntity<ApiResponse<Double>> getTotalPriceOfAllOrders() {
        try {
            Double totalPrice = orderService.getTotalPriceOfAllOrders();
            return ResponseEntity.ok(new ApiResponse<>("Total price of all orders retrieved successfully", HttpStatus.OK.value(), totalPrice));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Failed to retrieve total price of all orders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        }
    }

}

