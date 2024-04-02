/*package FINALEQUIFARM.EQUIFARM.EmailApp;

import FINALEQUIFARM.EQUIFARM.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verification")
public class EmailController {

    private final EmailSender emailSender;

    @Autowired
    public EmailController(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody Employee emailRequest) {
        // Perform null checks before invoking methods on the model
        if (emailRequest != null && emailRequest.getEmail() != null) {
            String toEmail = emailRequest.getEmail();
            String subject = "Verification Code";
            String text = "Your verification code is: " + emailRequest.getVerificationCode();
            emailSender.sendEmailWithVerificationCode(toEmail, subject, text); // Pass the required parameters
            return ResponseEntity.accepted().body("Email sent successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid email request: 'email' is null.");
        }
    }
}*/
