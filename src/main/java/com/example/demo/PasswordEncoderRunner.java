package com.example.demo;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class PasswordEncoderRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BCryptPasswordEncoder encoder= new BCryptPasswordEncoder();
		String password="Admin123";
		String encodePwd=encoder.encode(password);
		System.out.println(encodePwd);
	}

}
