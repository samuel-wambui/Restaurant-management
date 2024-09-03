package HotelManagement.model;


import HotelManagement.model.Employee;
import HotelManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> findById(long id) {
        return employeeRepository.findById(id);
    }

//    public Optional<Employee> findByUsername(String username) {
//        return employeeRepository.findByUsername(username);
//    }

    public Employee updateEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(long id) {
        employeeRepository.deleteById(id);
    }

    public String generateVerificationCode(long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.generateVerificationCode();
            employeeRepository.save(emp);
            return emp.getVerificationCode();
        }
        return null;
    }

    public boolean verifyCode(long id, String code) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            return emp.verifyCode(code);
        }
        return false;
    }

    public String generateResetPasswordVerificationCode(long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            return emp.generateResetPasswordVerificationCode();
        }
        return null;
    }

    public boolean validateResetPasswordCode(long id, String code) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            return emp.validateResetPasswordCode(code);
        }
        return false;
    }

    public void setEmployeeVerified(long id, boolean isVerified) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setVerifiedFlag(isVerified);
            employeeRepository.save(emp);
        }
    }

    public void setEmployeeLocked(long id, boolean isLocked) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setLockedFlag(isLocked);
            employeeRepository.save(emp);
        }
    }

    public void setEmployeeDeleted(long id, boolean isDeleted) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            emp.setDeletedFlag(isDeleted);
            employeeRepository.save(emp);
        }
    }

    private String generateCode(int length) {
        final String CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CODE_CHARACTERS.length());
            codeBuilder.append(CODE_CHARACTERS.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }
}
