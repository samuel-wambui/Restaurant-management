package HotelManagement.service;

import HotelManagement.model.Employee;
import HotelManagement.model.Role;

import java.util.List;

public interface EmployeeService {

    Employee saveEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(long id);
    Employee updateEmployee(Employee employee, long id);
    boolean deleteEmployee(long id);
    void assignRoles(Employee employee, List<Role> roles);


}
