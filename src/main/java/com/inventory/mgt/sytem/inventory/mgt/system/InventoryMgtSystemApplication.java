package com.inventory.mgt.sytem.inventory.mgt.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class InventoryMgtSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryMgtSystemApplication.class, args);
	}

	@Bean
	PasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}

}
