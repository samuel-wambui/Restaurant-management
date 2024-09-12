package HotelManagement.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Inventory> getAllItems() {
        return inventoryRepository.findAll();
    }

    public Inventory getItemById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public Inventory addItem(InventoryDto inventoryDto) {
        Inventory inventory = new Inventory();
        inventory.setItemName(inventoryDto.getItemName());
        inventory.setQuantity(inventoryDto.getQuantity());
        inventory.setCategory(inventoryDto.getCategory());
        inventory.setSupplier(inventoryDto.getSupplier());
        inventory.setUnit(inventoryDto.getUnit());
        inventory.setRecordedDate(inventoryDto.getRecordedDate());

        return inventoryRepository.save(inventory);
    }

    public Inventory updateItem(Long id, InventoryDto inventoryDto) {
        Inventory existingItem = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        existingItem.setItemName(inventoryDto.getItemName());
        existingItem.setQuantity(inventoryDto.getQuantity());
        existingItem.setCategory(inventoryDto.getCategory());
        existingItem.setSupplier(inventoryDto.getSupplier());
        existingItem.setUnit(inventoryDto.getUnit());
        existingItem.setRecordedDate(inventoryDto.getRecordedDate());

        return inventoryRepository.save(existingItem);
    }

    public void deleteItem(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Item not found");
        }
        inventoryRepository.deleteById(id);
    }
}
