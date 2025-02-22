package com.example.demo.controller;

import java.util.Optional;

//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Admin;
import com.example.demo.model.Holiday;
import com.example.demo.repo.AdminRepository;
import com.example.demo.service.HolidayService;

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
	@Autowired
	private HolidayService holidayService;
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
//	@GetMapping("/{page}")
//    public String loadPage(@PathVariable String page,Model modle) {
//		System.out.println("hellozzzz");
//        return "admin/"+page;
//    }
//	@GetMapping("/dashboardContent")
//	public String dashboardContent() {
//		System.out.println("Content is running");
//		return "admin/dashboardContent";
//	}
	@GetMapping("/addProfessor")
	public String addProfessor(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("showToastr","true");
	    model.addAttribute("toastrType","warning");
	    model.addAttribute("toastrMessage","The Professor ID create automatically");
		System.out.println("Add Professor is running");
		return "admin/addProfessor";
	}
	@GetMapping("/editProfessor")
	public String editProfessor(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Edit Professor is running");
		return "admin/editProfessor";
	}
	@GetMapping("/statusProfessor")
	public String statusProfessor(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Status Professor is running");
		return "admin/statusProfessor";
	}
	@GetMapping("/addStudent")
	public String addStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Add Student is running");
		return "admin/addStudent";
	}
	@GetMapping("/editStudent")
	public String editStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Edit Student is running");
		return "admin/editStudent";
	}
	@GetMapping("/statusStudent")
	public String statusStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Status Student is running");
		return "admin/statusStudent";
	}
	@GetMapping("/addDepartment")
	public String addDepartment(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("toastrMessage","Department Code create automatically");
		System.out.println(" Add Department is running");
		return "admin/addDepartment";
	}
	@GetMapping("/editDepartment")
	public String editDepartment(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Edit Department is running");
		return "admin/editDepartment";
	}
	@GetMapping("/addSubject")
	public String addSubject(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("toastrMessage","Subject ID create automatically you can change if you want");
		System.out.println(" Add Subject is running");
		return "admin/addSubject";
	}
	@GetMapping("/editSubject")
	public String editSubject(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Edit Subject is running");
		return "admin/editSubject";
	}
	@GetMapping("/semester")
	public String semester(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("toastrMessage","Once Semester Duration is created you cannot change");
		System.out.println(" Add Semester is running");
		return "admin/semester";
	}
	@GetMapping("/holiday")
	public String holiday(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Add Holiday is running");
		return "admin/holiday";
	}
	@PostMapping("/addHoliday")
	public ResponseEntity<String>addHoliday(@RequestBody Holiday holiday){
		System.out.println("Received holiday: " + holiday.getHolidayName() + ", " + holiday.getHolidayDate() + ", " + holiday.getHolidayType());
		String result=holidayService.addHoliday(holiday);
		if(result.startsWith("Error")) {
			//return ResponseEntity.status(400).body(result);
			return ResponseEntity.status(409).body(result);
		}else {
			return ResponseEntity.status(201).body(result);
		}
	}

	public class TestEndpointController {

	    @PostMapping("/test")
	    public ResponseEntity<String> testEndpoint(@RequestBody String payload) {
	        return ResponseEntity.ok("Test endpoint reached");
	    }
	}


}
