package HotelManagement.stock.Category;

import HotelManagement.Departments.DepartmentEntity;
import HotelManagement.Departments.DepartmentRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private DepartmentRepo departmentRepo;
    public Category createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setCategoryName(categoryDto.getCategoryName());
        Set<Long> departmentIds = categoryDto.getDepartmentIds();

        // Prepare a collection to hold validated departments.
        Set<DepartmentEntity> validDepartments = new HashSet<>();
        for (Long deptId : departmentIds) {
            DepartmentEntity department = departmentRepo.findById(deptId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Department with ID " + deptId + " is not found or is deleted"
                    ));
            if (department.isDeleted()) {
                throw new EntityNotFoundException(
                        "Department " + department.getDepartmentName() + " is not found or is deleted"
                );
            }
            validDepartments.add(department);
        }
        category.setDepartments(validDepartments);
        return categoryRepo.save(category);
    }

    public Category updateCategory(CategoryDto categoryDto, Long id) {
        Category existinCcategory = categoryRepo.findById(id).get();
        if (existinCcategory == null) {
            throw new IllegalArgumentException("Category with ID " + id + " not found");
        } else {

            existinCcategory.setCategoryName(categoryDto.getCategoryName());
            categoryRepo.save(existinCcategory);
        }
        return existinCcategory;
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepo.findById(id).get();
        if (category == null) {
            throw new IllegalArgumentException("Category with ID " + id + " not found");
        }
        category.setDeleted(true);
        categoryRepo.save(category);
    }

    public Category getCategoryById(Long id) {
        Optional<Category> categoryOptional = categoryRepo.findById(id);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        } else {
            throw new IllegalArgumentException("Category with ID " + id + " not found");
        }
    }

    public List<Category> getAllCategory() {
        return categoryRepo.findAll();
    }
}
