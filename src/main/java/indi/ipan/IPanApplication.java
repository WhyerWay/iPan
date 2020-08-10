package indi.ipan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class IPanApplication {

	public static void main(String[] args) {
		SpringApplication.run(IPanApplication.class, args);
	}
}
