package FINALEQUIFARM.EQUIFARM.service.impl;

import FINALEQUIFARM.EQUIFARM.EmailApp.EmailSender;
import FINALEQUIFARM.EQUIFARM.exemption.ExemptionNotFound;
import FINALEQUIFARM.EQUIFARM.model.Employee;
import FINALEQUIFARM.EQUIFARM.model.Role;
import FINALEQUIFARM.EQUIFARM.repository.EmployeeRepository;
import FINALEQUIFARM.EQUIFARM.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmailSender emailSender;


    @Override
    public Employee saveEmployee(Employee employee) {
        // You might want to validate employee details here before saving
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllByDeletedFalse();
    }

    @Override
    public Employee getEmployeeById(long id) {
        return employeeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ExemptionNotFound("Employee", "Id", id));
    }

    @Override
    public Employee updateEmployee(Employee employee, long id) {
        Employee existingEmployee = employeeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ExemptionNotFound("Employee", "Id", id));

        existingEmployee.setUsername(employee.getUsername());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setPhoneNumber(employee.getPhoneNumber());
        existingEmployee.setPassword(employee.getPassword());

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public boolean deleteEmployee(long id) {
        Employee employee = employeeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ExemptionNotFound("Employee", "Id", id));
        employee.setDeleted(true); // Ensure the deleted property is properly set
        employeeRepository.save(employee);
        return true;

    }


    @Override
    public void assignRoles(Employee employee, List<Role> roles) {
        employee.setRoles(roles);
        employeeRepository.save(employee);
    }




}
