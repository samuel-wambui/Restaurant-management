package HotelManagement.housekeeping;

import HotelManagement.rooms.RoomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class HousekeepingService {

    @Autowired
    private HousekeepingRepository housekeepingRepository;

    @Autowired
    private RoomsRepository roomsRepository;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public List<Housekeeping> getAllHousekeepingRecords() {
        return housekeepingRepository.findAll();
    }

    public Housekeeping getHousekeepingById(Long id) {
        return housekeepingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Housekeeping record not found"));
    }

    public Housekeeping createHousekeeping(HousekeepingDto housekeepingDto) throws ParseException {
        Housekeeping housekeeping = new Housekeeping();
        housekeeping.setRooms(roomsRepository.findById(housekeepingDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));
        housekeeping.setIsCleaned(housekeepingDto.getIsCleaned());
        housekeeping.setLastCleanedDate(dateFormat.parse(housekeepingDto.getLastCleanedDate()));

        return housekeepingRepository.save(housekeeping);
    }

    public Housekeeping updateHousekeeping(Long id, HousekeepingDto housekeepingDto) throws ParseException {
        Housekeeping housekeeping = housekeepingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Housekeeping record not found"));

        housekeeping.setRooms(roomsRepository.findById(housekeepingDto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found")));
        housekeeping.setIsCleaned(housekeepingDto.getIsCleaned());
        housekeeping.setLastCleanedDate(dateFormat.parse(housekeepingDto.getLastCleanedDate()));

        return housekeepingRepository.save(housekeeping);
    }

    public void deleteHousekeeping(Long id) {
        if (!housekeepingRepository.existsById(id)) {
            throw new RuntimeException("Housekeeping record not found");
        }
        housekeepingRepository.deleteById(id);
    }
}
