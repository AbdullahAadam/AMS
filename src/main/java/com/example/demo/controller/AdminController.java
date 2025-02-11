package com.example.demo.controller;

import java.util.Optional;

//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Admin;
import com.example.demo.repo.AdminRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {
//	@GetMapping("/dashboard")
//	public String dashboard(Model model,@AuthenticationPrincipal UserDetails userDetails){
//		model.addAttribute("username",userDetails.getUsername());
//		return "admin/dashboard";
//	}
	@Autowired
	private AdminRepository adminRepo;
	@Autowired
	private PasswordEncoder passwordEncode;
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/dashboard")
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
	//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/forgot")
	public String showForgot() {
		return "admin/forgot";
	}
	@PostMapping("/forgot")
	public String processForgotPassword(@RequestParam String email,
										@RequestParam String name,
										RedirectAttributes redirectAttributes) {
		System.out.println("Enterd email: "+email);
		System.out.println("Entered name: "+name);
		Optional<Admin> optAdmin=adminRepo.findByEmail(email);
		if(optAdmin.isEmpty()) {
			redirectAttributes.addFlashAttribute("emailError","Email does not Exit");
			return "redirect:/admin/forgot";
		}
		Admin admin=optAdmin.get();
		
		if(!admin.getName().equals(name)) {
			redirectAttributes.addFlashAttribute("nameError","Name is incorrect");
			return "redirect:/admin/forgot";
		}
		redirectAttributes.addFlashAttribute("email",email);
		return"redirect:/admin/reset";
		
	}
	//@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/reset")
	public String showResetPassword() {
		return "admin/reset";
	}
	@PostMapping("/reset")
	public String processResetPassword(@RequestParam String email,
										@RequestParam String pwd,
										@RequestParam String cpwd,
										RedirectAttributes redirectAttributes) {
		
		if(!cpwd.equals(pwd)) {
			redirectAttributes.addFlashAttribute("error","Passwords does not match");
			return "redirect:/admin/reset";
		}
		Optional<Admin> optAdmin=adminRepo.findByEmail(email);
		if(optAdmin.isEmpty()) {
			redirectAttributes.addFlashAttribute("error","Please follow the correct Order");
			return "redirect:/admin/forgot";
		}
		Admin admin=optAdmin.get();
		admin.setPwd(passwordEncode.encode(pwd));
		adminRepo.save(admin);
		redirectAttributes.addFlashAttribute("success","Password reset successfully");
		return "redirect:/admin/login";
	}


}
