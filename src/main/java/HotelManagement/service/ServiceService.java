package HotelManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<HotelManagement.service.Service> getAllServices() {
        return serviceRepository.findAll();
    }

    public HotelManagement.service.Service getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public HotelManagement.service.Service createService(ServiceDto serviceDto) {
        HotelManagement.service.Service service = new HotelManagement.service.Service();
        service.setServiceName(serviceDto.getServiceName());
        service.setDescription(serviceDto.getDescription());
        service.setPrice(serviceDto.getPrice());
        service.setServiceType(serviceDto.getServiceType());

        return serviceRepository.save(service);
    }

    public HotelManagement.service.Service updateService(Long id, ServiceDto serviceDto) {
        HotelManagement.service.Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        existingService.setServiceName(serviceDto.getServiceName());
        existingService.setDescription(serviceDto.getDescription());
        existingService.setPrice(serviceDto.getPrice());
        existingService.setServiceType(serviceDto.getServiceType());

        return serviceRepository.save(existingService);
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Service not found");
        }
        serviceRepository.deleteById(id);
    }
}
