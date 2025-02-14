package HotelManagement;

import HotelManagement.EmailApp.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HotelManagementApplicationTests {
	@Autowired
	private EmailSender emailSender;




}
