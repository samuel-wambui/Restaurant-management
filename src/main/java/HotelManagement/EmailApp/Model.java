package HotelManagement.EmailApp;

import HotelManagement.model.Employee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class Model {
    private Employee employee;
    private String verificationCode;
    private String to;
    private String subject = "EQUIFARM"; // Initialize subject
    private String text;

    // Default constructor
    public Model() {
    }

    @Autowired
    public Model(Employee employee) {
        this.employee = employee;
        if (employee != null) {
            this.to = employee.getEmail();
            this.text = "Hello " + employee.getUsername() + ", your verification code is " + employee.getVerificationCode();
        }
    }
}
