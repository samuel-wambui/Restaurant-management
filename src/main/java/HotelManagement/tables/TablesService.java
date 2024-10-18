package HotelManagement.tables;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TablesService {

    @Autowired
    private TablesRepository tablesRepository;

    public List<TablesEntity> getAllTables() {
        return tablesRepository.findAll();
    }

    public TablesEntity getTableById(Long id) {
        return tablesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
    }

    public TablesEntity createTable(TablesDto tablesDto) {
        TablesEntity table = new TablesEntity();
        table.setTableNumber(tablesDto.getTableNumber());
        table.setSeatingCapacity(tablesDto.getSeatingCapacity());
        return tablesRepository.save(table);
    }

    public TablesEntity updateTable(Long id, TablesDto tablesDto) {
        TablesEntity existingTable = tablesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));
        existingTable.setTableNumber(tablesDto.getTableNumber());
        existingTable.setSeatingCapacity(tablesDto.getSeatingCapacity());
        return tablesRepository.save(existingTable);
    }

    public void deleteTable(Long id) {
        if (!tablesRepository.existsById(id)) {
            throw new RuntimeException("Table not found");
        }
        tablesRepository.deleteById(id);
    }
}
