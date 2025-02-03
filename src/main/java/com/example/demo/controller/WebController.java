package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
	@GetMapping("/admin/login")
	public String adminLoginPage() {
		System.out.println("Admin Loging");
		return "admin/login";
	}
	@GetMapping("/professor/login")
	public String professorLoginPage() {
		return "professor/login";
	}
	@GetMapping("/student/login")
	public String studentLoginPage() {
		return "student/login";
	}

	
}
