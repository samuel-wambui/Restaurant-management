package HotelManagement;

import HotelManagement.EmailApp.EmailSender;
import HotelManagement.EmailApp.Model;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HotelManagementApplicationTests {
	@Autowired
	private EmailSender emailSender;

	@Autowired
	private Model model;


}
