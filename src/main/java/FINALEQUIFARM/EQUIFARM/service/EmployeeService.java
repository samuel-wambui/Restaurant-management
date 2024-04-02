package FINALEQUIFARM.EQUIFARM.service;

import FINALEQUIFARM.EQUIFARM.model.Employee;
import FINALEQUIFARM.EQUIFARM.model.Role;

import java.util.List;

public interface EmployeeService {

    Employee saveEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(long id);
    Employee updateEmployee(Employee employee, long id);
    void deleteEmployee(long id);
    void assignRoles(Employee employee, List<Role> roles);


}
