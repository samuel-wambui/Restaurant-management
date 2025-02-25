package HotelManagement.Departments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    @Autowired
    DepartmentRepo departmentRepo;
    public DepartmentEntity save(DepartmentDto departmentDto) {
        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setDepartmentName(departmentDto.getDepartmentName());
        departmentEntity.setDepartmentCode(generateDepartmentCode());
        departmentRepo.save(departmentEntity);
        return departmentEntity;

    }

    private String generateDepartmentCode() {
        String lastCode = departmentRepo.findLastDepartmentCode();

        int nextNumber = 1; // Default value
        if (lastCode != null && lastCode.startsWith("D")) {
            nextNumber = Integer.parseInt(lastCode.substring(1)) + 1;
        }

        return String.format("D%03d", nextNumber); // Generates D001, D002, etc.
    }

    public DepartmentEntity update(DepartmentDto departmentDto, Long id) {
        DepartmentEntity existingDepartment = departmentRepo.findById(id).orElse(null);
        if (existingDepartment == null) {
            throw new IllegalArgumentException("Department with ID " + id + " not found");
        }
        existingDepartment.setDepartmentName(departmentDto.getDepartmentName());
        departmentRepo.save(existingDepartment);
        return existingDepartment;

    }
     public DepartmentEntity getById(Long id) {
        return departmentRepo.findById(id).orElse(null);
    }
    public List<DepartmentEntity> getAllDepartments() {
        return departmentRepo.findAll();
    }

    public void deleteDepartment(Long id) {
     Optional<DepartmentEntity> optionalDepartmentEntity =   departmentRepo.findById(id);
     if (optionalDepartmentEntity.isPresent()) {
         DepartmentEntity departmentEntity = optionalDepartmentEntity.get();
         departmentEntity.setDeleted(true);
         departmentRepo.save(departmentEntity);
     }
    }


}


