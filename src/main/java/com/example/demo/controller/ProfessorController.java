package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/professor")
public class ProfessorController {
	@PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
	@GetMapping("/dashboard")
	public String dashboard(Model model,@AuthenticationPrincipal UserDetails userDetails) {
		model.addAttribute("username",userDetails.getUsername());
		return "professor/dashboard";
	}
	

}
