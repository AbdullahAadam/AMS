package com.example.demo.controller;

import java.util.Optional;

//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
//   import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Admin;
import com.example.demo.repo.AdminRepository;

@Controller
//@RequestMapping("/admin")
public class AdminController {
//	@GetMapping("/dashboard")
//	public String dashboard(Model model,@AuthenticationPrincipal UserDetails userDetails){
//		model.addAttribute("username",userDetails.getUsername());
//		return "admin/dashboard";
//	}
	@Autowired
	AdminRepository adminRepo;
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/admin/dashboard")
	/*public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    if (userDetails == null) {
	       System.out.println("No user is authenticated");
	    } else {
	        System.out.println("Authenticated user: " + userDetails.getUsername());
	    }
	    System.out.println("kfjdklsfk");
	    model.addAttribute("username", userDetails.getUsername());
	    return "admin/dashboard";
	}*/
	public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    if (userDetails == null) {
	       System.out.println("No user is authenticated");
	    } else {
	        System.out.println("Authenticated user: " + userDetails.getUsername());
	    }
	    System.out.println("kfjdklsfk");
	    String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    return "admin/dashboard";
	}
	@GetMapping("/admin/forgot")
	public String showForgot() {
		return "admin/forgot";
	}


}
