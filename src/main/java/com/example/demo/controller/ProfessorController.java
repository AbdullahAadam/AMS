package com.example.demo.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.model.Holiday;
import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.repo.HolidayRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.StudentService;

@Controller
@RequestMapping("/professor")
public class ProfessorController {
	@Autowired
	private ProfessorRepository profRepo;
	@Autowired
	private HolidayRepository holRepo;
	@Autowired
	private PasswordEncoder passwordEncode;
	@Autowired
	private ProfessorService profService;
	@Autowired
	private DepartmentService deptService;
	@Autowired
	private StudentService studService;
	@PreAuthorize("hasAuthority('ROLE_PROFESSOR')")
	@GetMapping("/dashboard")
	public String dashboard(Model model,@AuthenticationPrincipal UserDetails userDetails) {
		if (userDetails == null) {
		       System.out.println("No user is authenticated");
		    } else {
		        System.out.println("Authenticated user: " + userDetails.getUsername());
		    }
		String email=userDetails.getUsername();
		Optional<Professor>optProfessor=profRepo.findByEmail(email);
		Professor prof=optProfessor.get();
		String role=prof.getRole();
		String text="Total Sessions:";
		Long period=deptService.getTotalPeriod(prof.getDepartment().getDeptId());
		List<Student>approvalStatusList=new ArrayList<>();
		if("prof".equalsIgnoreCase(role)) {
			role="Professor";
			approvalStatusList=studService.getPendingMentees(prof.getProfId());
		}else if ("hod".equalsIgnoreCase(role)) {
			 text=" Managed Professors Count:";
			 period=profService.getManagedProfessorsCount(prof.getDepartment().getDeptId())-1;
			 approvalStatusList=studService.getPendingDepartmentStudents(prof.getDepartment().getDeptId());
		}
		model.addAttribute("username",prof.getName());
		model.addAttribute("email",prof.getEmail());
		model.addAttribute("profId",prof.getProfId());
		model.addAttribute("role",role);
		model.addAttribute("mentee",profService.getMenteeCount(prof.getProfId()));
		model.addAttribute("dept",profService.getProfessorDepartmentCount(prof.getProfId()));
		model.addAttribute("period",period);
		model.addAttribute("text",text);
		model.addAttribute("students",approvalStatusList);
		return "professor/dashboard";
	}
	@GetMapping("/forgot")
	public String showForgot() {
		return "professor/forgot";
	}
	@PostMapping("/forgot")
	public String processForgotPassword(@RequestParam String profId,
										@RequestParam String email,
										RedirectAttributes redirectAttributes) {
		System.out.println("Enterd email: "+email);
		System.out.println("Entered name: "+profId);
		Optional<Professor> optProfessorId=profRepo.findById(profId);
		if(optProfessorId.isEmpty()) {
			redirectAttributes.addFlashAttribute("profIdError","Invalid Professor ID");
			return "redirect:/professor/forgot";
		}
		
		Optional<Professor> optProfessor=profRepo.findByEmail(email);
		if(optProfessor.isEmpty()) {
			redirectAttributes.addFlashAttribute("emailError","Email does not Exit");
			return "redirect:/professor/forgot";
		}
		
		redirectAttributes.addFlashAttribute("email",email);
		return"redirect:/professor/reset";
		
	}
	@GetMapping("/reset")
	public String showResetPassword() {
		return "professor/reset";
	}
	@PostMapping("/reset")
	public String processResetPassword(@RequestParam String email,
										@RequestParam String pwd,
										@RequestParam String cpwd,
										RedirectAttributes redirectAttributes) {
		
		Optional<Professor> optProfessor=profRepo.findByEmail(email);
		if(optProfessor.isEmpty()) {
			redirectAttributes.addFlashAttribute("error","Please follow the correct Order");
			return "redirect:/professor/forgot";
		}
		
		if(!cpwd.equals(pwd)) {
			redirectAttributes.addFlashAttribute("error","Passwords does not match");
			return "redirect:/professor/reset";
		}		
		Professor prof=optProfessor.get();
		prof.setPwd(passwordEncode.encode(pwd));
		profRepo.save(prof);
		redirectAttributes.addFlashAttribute("success","Password reset successfully");
		return "redirect:/professor/login";
	}
	@GetMapping("/today-onward")
    public ResponseEntity<List<Holiday>> getTodayOnwardHolidays() {
        LocalDate today = LocalDate.now();

        // Fetch upcoming holidays from the database
        List<Holiday> upcomingHolidays = holRepo.findUpcomingHolidays(today);

        // Add upcoming Sundays dynamically
        LocalDate nextSunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        for (int i = 0; i < 4; i++) {  // Fetch next 4 Sundays
            if (!nextSunday.isBefore(today)) {  // Only add if it's today or later
                Holiday sunday = new Holiday();
                sunday.setHolidayName("Sunday");
                sunday.setHolidayDate(nextSunday);
                sunday.setHolidayType("Weekly Off");
                upcomingHolidays.add(sunday);
            }
            nextSunday = nextSunday.plusWeeks(1); // Move to the next Sunday
        }

        // Sort holidays by date
        upcomingHolidays.sort(Comparator.comparing(Holiday::getHolidayDate));

        return ResponseEntity.ok(upcomingHolidays);
    }
	@GetMapping("/statusStudent")
	public String statusStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
		Optional<Professor>optProfessor=profRepo.findByEmail(email);
		Professor prof=optProfessor.get();
		String role=prof.getRole();
		List<Student>approvalStatusList=new ArrayList<>();
		if("prof".equalsIgnoreCase(role)) {
			role="Professor";
			approvalStatusList=studService.getPendingMentees(prof.getProfId());
		}else if ("hod".equalsIgnoreCase(role)) {
			 approvalStatusList=studService.getPendingDepartmentStudents(prof.getDepartment().getDeptId());
		}
	    model.addAttribute("username",prof.getName());
	    model.addAttribute("email",prof.getEmail());
	    model.addAttribute("profId",prof.getProfId());
	    model.addAttribute("role",role);
	    model.addAttribute("students",approvalStatusList);
		System.out.println(" Status Student is running");
		return "professor/statusStudent";
	}
	@PutMapping("/acceptStudent/{regNo}")
	public ResponseEntity<?> acceptStudent(@PathVariable String regNo,@AuthenticationPrincipal UserDetails userDetails) {
	    try {
	    	String email=userDetails.getUsername();
			Optional<Professor>optProfessor=profRepo.findByEmail(email);
			Professor prof=optProfessor.get();
			if (prof == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approver not found");
	        }
			String name=prof.getName();
			String role = prof.getRole().equalsIgnoreCase("hod") ? "HOD" : "Professor";
	    	studService.acceptStudent(regNo,name,role);
	        return ResponseEntity.ok("Student accepted successfully.");
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Error accepting student: " + ex.getMessage());
	    }
	}
	@DeleteMapping("/rejectStudent/{regNo}")
	public ResponseEntity<?> rejectStudent(@PathVariable String regNo) {
	    try {
	        studService.rejectedStudent(regNo);
	        return ResponseEntity.ok("Student rejected successfully.");
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Error rejected student: " + ex.getMessage());
	    }
	}
}
