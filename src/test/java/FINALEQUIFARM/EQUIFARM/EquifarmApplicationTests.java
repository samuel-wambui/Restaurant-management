package FINALEQUIFARM.EQUIFARM;

import FINALEQUIFARM.EQUIFARM.EmailApp.EmailSender;
import FINALEQUIFARM.EQUIFARM.EmailApp.Model;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EquifarmApplicationTests {
	@Autowired
	private EmailSender emailSender;

	@Autowired
	private Model model;

	@Test
	void sendEmailWithVerificationCode() {
		// Call the sendEmailWithVerificationCode() method of EmailSender
		emailSender.sendEmailWithVerificationCode(model.getTo(), model.getSubject(), model.getText());
	}
}
