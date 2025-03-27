package com.example.demo.controller;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.PassOutStudentDTO;
import com.example.demo.dto.ProfStudentResponseDTO;
import com.example.demo.dto.ProfStudentUpdateDTO;
import com.example.demo.model.Holiday;
import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.repo.HolidayRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.StudentRepository;
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
	@Autowired
	private StudentRepository studRepo;
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
	@GetMapping("/editStudent")
	public String editStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
		Optional<Professor>optProfessor=profRepo.findByEmail(email);
		Professor prof=optProfessor.get();
		String role=prof.getRole();
		List<Student>approvalStatusList=new ArrayList<>();
		if("prof".equalsIgnoreCase(role)) {
			role="Professor";
			approvalStatusList=studService.getApprovelMentees(prof.getProfId());
		}else if ("hod".equalsIgnoreCase(role)) {
			 approvalStatusList=studService.getApprovelDepartmentStudents(prof.getDepartment().getDeptId());
		}
	    model.addAttribute("username",prof.getName());
	    model.addAttribute("email",prof.getEmail());
	    model.addAttribute("profId",prof.getProfId());
	    model.addAttribute("role",role);
	    model.addAttribute("students",approvalStatusList);
		System.out.println(" Edit Student is running");
		return "professor/editStudent";
	}
	@GetMapping("/mentee")
	public String mentee(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
		Optional<Professor>optProfessor=profRepo.findByEmail(email);
		Professor prof=optProfessor.get();
		String role=prof.getRole();
		String stud_ment="Mentees";
		List<Student>approvalStatusList=new ArrayList<>();
		if("prof".equalsIgnoreCase(role)) {
			role="Professor";			
			approvalStatusList=studService.getApprovelMentees(prof.getProfId());
		}else if ("hod".equalsIgnoreCase(role)) {
			stud_ment="Students";
			 approvalStatusList=studService.getApprovelDepartmentStudents(prof.getDepartment().getDeptId());
		}
		Long years=prof.getDepartment().getYear();
	    model.addAttribute("username",prof.getName());
	    model.addAttribute("email",prof.getEmail());
	    model.addAttribute("profId",prof.getProfId());
	    model.addAttribute("role",role);
	    model.addAttribute("stud_ment",stud_ment);
	    model.addAttribute("students",approvalStatusList);
	    model.addAttribute("totalYears", years);
		System.out.println(" Edit Student is running");		
		return "professor/mentee";
	}
	@GetMapping("/oldStudent")
	public String showOldStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
	    String email = userDetails.getUsername();
	    Optional<Professor> optProfessor = profRepo.findByEmail(email);

	    if (optProfessor.isEmpty()) {
	        return "redirect:/error"; // Handle case where professor is not found
	    }

	    Professor prof = optProfessor.get();
	    String role = prof.getRole();
	    List<Student> oldStudentList;

	    if ("prof".equalsIgnoreCase(role)) {
	        role = "Professor";
	        oldStudentList = studRepo.findByMentorProfIdAndCurrentYearAndCurrentSemester(prof.getProfId(), "FINISHED", "FINISHED");
	    } else if ("hod".equalsIgnoreCase(role)) {
	        oldStudentList = studRepo.findByDepartmentDeptIdAndCurrentYearAndCurrentSemester(prof.getDepartment().getDeptId(), "FINISHED", "FINISHED");
	    } else {
	        oldStudentList = List.of();
	    }

	    // Convert students to DTOs with computed batch year range
	    List<PassOutStudentDTO> passoutStudents = oldStudentList.stream().map(student -> 
	        new PassOutStudentDTO(
	            student.getName(),
	            student.getRegNo(),
	            student.getDepartment().getDeptName(),
	            student.getPhone(),
	            student.getImg(),
	            student.getJoinDate(),
	            computeBatchYear(student) // Compute batch display format
	        )
	    ).collect(Collectors.toList());

	    // Add attributes to the model
	    model.addAttribute("username", prof.getName());
	    model.addAttribute("email", prof.getEmail());
	    model.addAttribute("profId", prof.getProfId());
	    model.addAttribute("role", role);
	    model.addAttribute("passoutStudents", passoutStudents);

	    System.out.println("Passout Students is running");

	    return "professor/oldStudent";
	}

	// Method to compute batch format: "2020-2023(A)"
	private String computeBatchYear(Student student) {
	    if (student.getJoinDate() == null || student.getDepartment() == null) {
	        return "Unknown";
	    }

	    // ✅ Correct conversion of java.sql.Date to LocalDate
	    LocalDate joiningDate = ((java.sql.Date) student.getJoinDate()).toLocalDate();


	    int joiningYear = joiningDate.getYear();
	    int duration = Math.toIntExact(student.getDepartment().getYear()); // Course duration (e.g., 3 or 4 years)
	    int passingYear = joiningYear + duration;

	    // ✅ Safely handle batch field (A, B, C, D)
	    String batchLetter = (student.getBatch() != null) ? student.getBatch() : "Unknown";

	    return joiningYear + " - " + passingYear + " (" + batchLetter + ")";
	}
	@GetMapping("/student/edit/{regNo}")
	public ResponseEntity<ProfStudentResponseDTO>getStudentsByRegNo(@PathVariable String regNo){
		ProfStudentResponseDTO stud=studService.getStudentsById(regNo);
		if(stud!=null) {
			return ResponseEntity.ok(stud);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	@PutMapping("/student/update/{regNo}")
	public ResponseEntity<String>updateStudents(@PathVariable String regNo,@RequestBody ProfStudentUpdateDTO updateStudent) throws ParseException{
		System.out.print("Reciver joindate form fronted:????????????? "+updateStudent.getJoinDate());
		boolean success=studService.updateStudents(regNo, updateStudent);
		if(success) {
			return ResponseEntity.ok("Student updated Successfully");
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update Student.");
		}
	}
	@DeleteMapping("/student/delete/{regNo}")
	public ResponseEntity<String>deleteStudent(@PathVariable String regNo){
		try {
			studService.deleteStudent(regNo);
			return ResponseEntity.ok("Student Deleted successfully");
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Deleting in student"+e.getMessage());
			
		}
	}
	@GetMapping("/student/updateYear")
	public ResponseEntity<Map<String, String>> updateStudentYear(
	        @RequestParam String regNo,
	        @RequestParam String newJoinDate) {

	    Optional<Student> optionalStudent = studRepo.findById(regNo);

	    if (optionalStudent.isPresent()) {
	        Student student = optionalStudent.get();

	        // Convert String -> LocalDate -> Date
	        LocalDate localJoinDate = LocalDate.parse(newJoinDate);
	        Date convertedDate = java.sql.Date.valueOf(localJoinDate);

	        student.setJoinDate(convertedDate); // Now it's compatible
	        studService.updateCurrentYearAndSemester(student);
	        studRepo.save(student);

	        // Assuming this returns a Roman numeral (String)
	        /*String calculatedYear = studService.updateStudentYearAndSemester(regNo);
	        student.setCurrentYear(calculatedYear);*/

	        Map<String, String> response = new HashMap<>();
	        response.put("year", student.getCurrentYear()); 
	        response.put("semester",student.getCurrentSemester());

	        return ResponseEntity.ok(response);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
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
