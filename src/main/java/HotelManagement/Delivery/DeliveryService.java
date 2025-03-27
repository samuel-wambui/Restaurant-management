package HotelManagement.Delivery;

import HotelManagement.foodorder.FoodOrder;
import HotelManagement.foodorder.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DeliveryService(DeliveryRepository deliveryRepository, OrderRepository orderRepository) {
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
    }

    public Delivery createDelivery(Long orderId, Delivery delivery) {
        Optional<FoodOrder> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            delivery.setOrder(orderOptional.get());
            delivery.setStatus(DeliveryStatus.PENDING);
            return deliveryRepository.save(delivery);
        } else {
            throw new RuntimeException("Order not found");
        }
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Optional<Delivery> getDeliveryById(Long id) {
        return deliveryRepository.findById(id);
    }

    public List<Delivery> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status);
    }

    public Delivery updateDeliveryStatus(Long id, DeliveryStatus status) {
        Optional<Delivery> deliveryOptional = deliveryRepository.findById(id);
        if (deliveryOptional.isPresent()) {
            Delivery delivery = deliveryOptional.get();
            delivery.setStatus(status);
            return deliveryRepository.save(delivery);
        } else {
            throw new RuntimeException("Delivery not found");
        }
    }

    public void deleteDelivery(Long id) {
        if (deliveryRepository.existsById(id)) {
            deliveryRepository.deleteById(id);
        } else {
            throw new RuntimeException("Delivery not found");
        }
    }
}
