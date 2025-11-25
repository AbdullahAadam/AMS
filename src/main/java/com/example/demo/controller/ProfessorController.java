package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.AttendSubjectDTO;

import com.example.demo.dto.AttendanceRequestDTO;
import com.example.demo.dto.AttendanceResponseDTO;
import com.example.demo.dto.AttendanceValidationRequest;
import com.example.demo.dto.AttendanceValidationResponse;
import com.example.demo.dto.PassOutStudentDTO;
import com.example.demo.dto.ProfStudentResponseDTO;
import com.example.demo.dto.ProfStudentUpdateDTO;
import com.example.demo.dto.StudentAttendanceDTO;
import com.example.demo.enums.LogStatus;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceId;
import com.example.demo.model.Department;
import com.example.demo.model.Holiday;
import com.example.demo.model.Professor;
import com.example.demo.model.Report;
import com.example.demo.model.Semester;
import com.example.demo.model.Student;
import com.example.demo.model.Subject;
import com.example.demo.repo.AttendanceRepository;
import com.example.demo.repo.DepartmentRepository;
import com.example.demo.repo.HolidayRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.ReportRepository;
import com.example.demo.repo.StudentRepository;
import com.example.demo.repo.SubjectRepository;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.SemesterService;
import com.example.demo.service.StudentService;
import com.example.demo.service.SubjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/professor")
public class ProfessorController {
	@Autowired
	private ProfessorRepository profRepo;
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private SubjectRepository subRepo;
	@Autowired
	private ReportRepository reportRepo;
	//@Autowired
	///private SemesterRepository semRepo;
	@Autowired
	private HolidayRepository holRepo;
	@Autowired
	private PasswordEncoder passwordEncode;
	@Autowired
	private ProfessorService profService;
	@Autowired
	private DepartmentService deptService;
	@Autowired
	private SemesterService semService;
	@Autowired
	private StudentService studService;
	@Autowired
	private SubjectService subService;
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private StudentRepository studRepo;
	@Autowired
	private AttendanceRepository attendanceRepo;
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
		String profImg=prof.getImg();
		String text="Total Sessions:";
		String menteStud="My Mentee";
		int markedSessions = attendanceService.getTodayMarkedSessions(prof.getProfId());
		long mentStudCount = studRepo.countByMentorProfIdAndLogStatus(prof.getProfId(), LogStatus.APPROVED);		
		Long period=deptService.getTotalPeriod(prof.getDepartment().getDeptId());
		List<Student>approvalStatusList=new ArrayList<>();
		if("prof".equalsIgnoreCase(role)) {
			role="Professor";
			
			approvalStatusList=studService.getPendingMentees(prof.getProfId());
		}else if ("hod".equalsIgnoreCase(role)) {
			 text=" Managed Professors Count:";
			 menteStud="My Students";
			 mentStudCount=studRepo.countByDepartmentDeptIdAndLogStatus(prof.getDepartment().getDeptId(), LogStatus.APPROVED);
			 period=profService.getManagedProfessorsCount(prof.getDepartment().getDeptId())-1;
			 approvalStatusList=studService.getPendingDepartmentStudents(prof.getDepartment().getDeptId());
		}
		model.addAttribute("username",prof.getName());
		model.addAttribute("email",prof.getEmail());
		model.addAttribute("profId",prof.getProfId());
		model.addAttribute("role",role);
		model.addAttribute("profImg",profImg);
		model.addAttribute("mentStudCount",mentStudCount);
		model.addAttribute("dept",profService.getProfessorDepartmentCount(prof.getProfId()));
		model.addAttribute("markedSessions", markedSessions);
		model.addAttribute("period",period);
		model.addAttribute("text",text);
		model.addAttribute("menteStud",menteStud);
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
	@GetMapping("/createAttendance")
	public String createAttendance(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
		Optional<Professor>optProfessor=profRepo.findByEmail(email);
		Professor prof=optProfessor.get();
		String role=prof.getRole();	
		String profImg=prof.getImg();
		if("prof".equalsIgnoreCase(role)) {
			role="Professor";			
		}
	    model.addAttribute("username",prof.getName());
	    model.addAttribute("email",prof.getEmail());
	    model.addAttribute("profId",prof.getProfId());
	    model.addAttribute("role",role);
	    model.addAttribute("profImg",profImg);
		System.out.println("Create Attendance is running");
		return "professor/createAttendance";
	}
	@GetMapping("/statusAttendance")
	public String markingAttendance(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
		Optional<Professor>optProfessor=profRepo.findByEmail(email);
		Professor prof=optProfessor.get();
		String role=prof.getRole();
		String profImg=prof.getImg();
		String stud_ment="Mentees";
		
		if("prof".equalsIgnoreCase(role)) {
			role="Professor";			
			
		}else if ("hod".equalsIgnoreCase(role)) {
			stud_ment="Students";
			
		}		
	    model.addAttribute("username",prof.getName());
	    model.addAttribute("email",prof.getEmail());
	    model.addAttribute("profId",prof.getProfId());
	    model.addAttribute("role",role);
	    model.addAttribute("profImg",profImg);
	    model.addAttribute("stud_ment",stud_ment);
		System.out.println("Student Attendance is running");		
		return "professor/marking";
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
		String profImg=prof.getImg();
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
	    model.addAttribute("profImg",profImg);
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
		String profImg=prof.getImg();
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
	    model.addAttribute("profImg",profImg);
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
		String profImg=prof.getImg();
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
	    model.addAttribute("profImg",profImg);
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
	    String profImg=prof.getImg();
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
	            computeBatchYear(student) 
	        )
	    ).collect(Collectors.toList());
	    model.addAttribute("username", prof.getName());
	    model.addAttribute("email", prof.getEmail());
	    model.addAttribute("profId", prof.getProfId());
	    model.addAttribute("role", role);
	    model.addAttribute("profImg", profImg);
	    model.addAttribute("passoutStudents", passoutStudents);

	    System.out.println("Passout Students is running");

	    return "professor/oldStudent";
	}

	// Method to compute batch format: "2020-2023(A)"
	private String computeBatchYear(Student student) {
	    if (student.getJoinDate() == null || student.getDepartment() == null) {
	        return "Unknown";
	    }

	    
	    LocalDate joiningDate = ((java.sql.Date) student.getJoinDate()).toLocalDate();


	    int joiningYear = joiningDate.getYear();
	    int duration = Math.toIntExact(student.getDepartment().getYear()); // Course duration (e.g., 3 or 4 years)
	    int passingYear = joiningYear + duration;

	    //  Safely handle batch field (A, B, C, D)
	    String batchLetter = (student.getBatch() != null) ? student.getBatch() : "Not Assigned";

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
	//Getting Departments
	 @GetMapping("/departments")
	    public ResponseEntity<List<Map<String, String>>> getProfessorDepartments(@AuthenticationPrincipal UserDetails userDetails) {
		 if (userDetails == null) {
		       System.out.println("No user is authenticated");
		    } else {
		        System.out.println("Authenticated user: " + userDetails.getUsername());
		    }
		String email=userDetails.getUsername();
		Optional<Professor>optProfessor=profRepo.findByEmail(email);
		Professor professor=optProfessor.get();   
		 List<Map<String, String>> departmentList = new ArrayList<>();

	        String role=professor.getRole();
	        if ("HOD".equalsIgnoreCase(role)) {
	            departmentList.add(Map.of(
	                "id", professor.getDepartment().getDeptId(),
	                "name", professor.getDepartment().getDeptName()
	            ));
	        } else {
	            // If professor teaches subjects, return departments of assigned subjects
	            departmentList = profService.getDepartmentsByProfessor(professor.getProfId());
	        }

	        return ResponseEntity.ok(departmentList);
	    }
	 //Get year based on dept
	 @GetMapping("/department/{deptId}")
	 public ResponseEntity<Map<String, Object>> getDepartmentDetails(@PathVariable String deptId) {
	     Optional<Department> departmentOpt = deptRepo.findById(deptId);
	     if (departmentOpt.isEmpty()) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Department not found"));
	     }
	     
	     Department department = departmentOpt.get();
	     Map<String, Object> response = new HashMap<>();
	     response.put("id", department.getDeptId());
	     response.put("name", department.getDeptName());
	     response.put("sem", department.getSem());
	     response.put("period", department.getPeriod());
	     
	     return ResponseEntity.ok(response);
	 }
	 
	 ///BackUp LATER
	 @GetMapping("/subjects/byYear")
	    public ResponseEntity<List<Map<String, String>>> getSubjectsByYear(
	            @RequestParam String deptId, @RequestParam String year) {

	        Department department = deptRepo.findById(deptId).orElse(null);
	        if (department == null) {
	            return ResponseEntity.status(404).body(Collections.emptyList());
	        }

	        int totalYears = department.getYear().intValue();

	        int selectedYear = "FINAL".equalsIgnoreCase(year) ? totalYears : Integer.parseInt(year);

	        int startSem = (selectedYear - 1) * 2 + 1;  // Year 1 → Sem 1 & 2, Year 2 → Sem 3 & 4
	        int endSem = startSem + 1;

	        List<Subject> subjects = subRepo.findSubjectsByDepartmentAndYear(deptId, startSem, endSem);

	        List<Map<String, String>> subjectList = subjects.stream()
	                .map(s -> Map.of("subId", s.getSubId(), "name", s.getName()))
	                .collect(Collectors.toList());

	        return ResponseEntity.ok(subjectList);
	   }
	//	Subject based on SemNo
	 

	 private static final Logger logger = LoggerFactory.getLogger(ProfessorController.class);

	 @GetMapping("/subjects/{semNo}/{deptId}")
	 public ResponseEntity<?> getSubjectsBySemester(
	         @PathVariable Long semNo, 
	         @PathVariable String deptId,
	         @AuthenticationPrincipal UserDetails userDetails) {

	     logger.info("📥 Received request: semNo = {}, deptId = {}", semNo, deptId);

	     // Fetch professor by email
	     String email = userDetails.getUsername();
	     Optional<Professor> optProfessor = profRepo.findByEmail(email);

	     if (optProfessor.isEmpty()) {
	         logger.warn("❌ Professor not found for email: {}", email);
	         return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                 .body(Map.of("error", "Access Denied: Professor not found."));
	     }

	     Professor prof = optProfessor.get();
	     logger.info("👨‍🏫 Professor found: ID = {}, Role = {}", prof.getProfId(), prof.getRole());

	     List<AttendSubjectDTO> subjects;
	     
	     if ("hod".equalsIgnoreCase(prof.getRole())) {
	         subjects = subService.findActiveSubjectsBySemesterNo(semNo, deptId);
	     } else {
	         subjects = subService.getSubjectsForProfessor(prof.getProfId(), semNo, deptId);
	     }

	     
	     if (subjects.isEmpty()) {
	    	 logger.warn("⚠️ No subjects found for professor: {}, semNo = {}, deptId = {}", prof.getProfId(), semNo, deptId);
	         Map<String, String> errorResponse = new HashMap<>();
	         errorResponse.put("error", "❌ No subjects are assigned for the selected semester and department.");
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	     }

	     logger.info("✅ Found {} subjects", subjects.size());
	     return ResponseEntity.ok(subjects);
	 }
	 
	/* @PostMapping("/check-attendance")
	 public ResponseEntity<?> checkAttendance(@RequestBody AttendanceCheckDTO attendanceCheckDTO) {
	     logger.info("📥 Received DTO: {}", attendanceCheckDTO);

	     logger.info("🔹 semNo: {}", attendanceCheckDTO.getSemNo());
	     logger.info("🔹 batch: {}", attendanceCheckDTO.getBatch());
	     //logger.info("🔹 subCode: {}", attendanceCheckDTO.getSubCode());
	     logger.info("🔹 attDate: {}", attendanceCheckDTO.getAttDate());
	     logger.info("🔹 period: {}", attendanceCheckDTO.getPeriod());

	     // Now validate after logging
	     if (attendanceCheckDTO.getAttDate() == null ) {
	         logger.error("❌ Missing required fields: attDate={}, subCode={}", 
	                       attendanceCheckDTO.getAttDate() /*,attendanceCheckDTO.getSubCode()*//*);
	         return ResponseEntity.badRequest().body("Missing required fields.");
	     }

	     return ResponseEntity.ok("✅ Attendance slot available.");
	 }*/
	 @PostMapping("/validate")
	    public ResponseEntity<?> validateSlot(@RequestBody AttendanceValidationRequest request) {
	        try {
	            AttendanceValidationResponse response = attendanceService.validateAttendanceSlot(request);
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        }
	    }




	 
	 @GetMapping("/students/{deptId}/{semNo}/{batch}")
	 @ResponseBody
	 public ResponseEntity<?> getStudents(
	         @PathVariable String deptId,
	         @PathVariable String semNo,
	         @PathVariable String batch) {

	     List<Student> students = studService.findStudentsByDeptSemBatch(deptId, semNo, batch);
	     System.out.println("Fetched Students from DB: " + students);
	     if (students.isEmpty()) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                              .body("No students found for the selected batch.");
	     }

	     return ResponseEntity.ok(students); // Returns 200 OK with the student list
	 }

	 @PostMapping("/mark-attendance")
	 public String markAttendancePageShow(
	         @RequestParam("markingDate") String markingDate,
	         @RequestParam("period") Long period,  // Fixed from 'period' to 'hour'
	         @RequestParam("subjectId") String subjectId,
	         @RequestParam("department") String department,
	         @RequestParam("semester") String semester,
	         @RequestParam("batch") String batch,
	         @RequestParam(value = "students", required = false, defaultValue = "[]") String studentsJson, // Handle missing parameter
	         Model model,@AuthenticationPrincipal UserDetails userDetails) {

		  System.out.println("Received Students JSON: " + studentsJson);
		 ObjectMapper objectMapper = new ObjectMapper();
		 objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // ✅ Ignore extra JSON fields
		 List<StudentAttendanceDTO> students;  // Change to List<String> if only roll numbers are sent

	     try {
	    	 students = objectMapper.readValue(studentsJson, new TypeReference<List<StudentAttendanceDTO>>() {});
	     } catch (JsonProcessingException e) {
	         e.printStackTrace();
	         students  = new ArrayList<>();
	     }

	     System.out.println("Parsed Students List: " + students);
	     
	     System.out.println("Semester "+semester);
	     String semNo = semester.replaceAll("[^0-9]", "");
	     System.out.println("Semester parts:"+semNo);
	     
	     // Add data to the model for Thymeleaf
	     model.addAttribute("markingDate", markingDate);
	     model.addAttribute("period", period);  // Fixed attribute name
	     model.addAttribute("subjectId", subjectId);
	     model.addAttribute("department", department);
	     model.addAttribute("semester", semester);
	     model.addAttribute("semNo", semNo);
	     model.addAttribute("batch", batch);
	     
	     String email=userDetails.getUsername();
		 Optional<Professor>optProfessor=profRepo.findByEmail(email);
		 Professor prof=optProfessor.get();
			String role=prof.getRole();
			String profImg=prof.getImg();
			if("prof".equalsIgnoreCase(role)) {
				role="Professor";			
			}
		    model.addAttribute("username",prof.getName());
		    model.addAttribute("email",prof.getEmail());
		    model.addAttribute("profId",prof.getProfId());
		    model.addAttribute("role",role);
		    model.addAttribute("profImg",profImg);
		    for (StudentAttendanceDTO student : students) {
		        AttendanceId attendanceId = new AttendanceId(
		            student.getRegNo(), subjectId, LocalDate.parse(markingDate), period
		        );

		        Optional<Attendance> optAttendance = attendanceRepo.findById(attendanceId);
		        if (optAttendance.isPresent()) {
		            Attendance attendance = optAttendance.get();
		            student.setLocked(attendance.isLocked());
		            student.setMarkedBy(attendance.getMarkedBy());
		            //student.setBatch(attendance.getBatch());
		        } else {
		        	
		            student.setLocked(false); // Default: Unlocked if no attendance exists
		            student.setMarkedBy(null); // Default: No one has marked yet
		        }
		    }
		 model.addAttribute("students", students);
	     return "professor/marking"; // Fixed Thymeleaf return path
	 }
	 
	 @GetMapping("/get-attendance")
	 public ResponseEntity<List<AttendanceResponseDTO>> getAttendance(
	         @RequestParam String subId,
	         @RequestParam Long semNo,
	         @RequestParam String attDate,
	         @RequestParam Long period) {
		 
		 LocalDate date = LocalDate.parse(attDate);
	     List<Attendance> attendanceList = attendanceRepo.findById_SubCodeAndId_AttDateAndId_Period(subId, date, period);

	     List<AttendanceResponseDTO> responseDTOs = attendanceList.stream().map(attendance -> new AttendanceResponseDTO(
	             attendance.getId().getRegNo(),
	             attendance.getStatus().toString(),
	             attendance.getMarkedBy(),
	             attendance.getMarkedByUser(),
	             attendance.getBatch(),
	             attendance.isLocked()
	     )).collect(Collectors.toList());

	     return ResponseEntity.ok(responseDTOs);
	 }


	 @PostMapping("/mark-attendanced")
	    public ResponseEntity<String> markAttendance(@RequestBody AttendanceRequestDTO request) {
	        try {
	            attendanceService.markAttendance(request);
	            attendanceService.updateReport(request.getRegNo(), request.getSubId(), request.getSemNo());
	            return ResponseEntity.ok("Attendance marked successfully");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Error marking attendance: " + e.getMessage());
	        }
	    }
	 @GetMapping("/viewMentee")
	 public String viewMentee(@RequestParam String regNo, Model model, @AuthenticationPrincipal UserDetails userDetails) {
	     // Fetch student and handle if not found
	     Student student = studRepo.findById(regNo)
	             .orElse(null);
	     if (student == null) {
	         return "redirect:/professor/mentee"; // Redirect if student not found
	     }

	     String currentSemester = student.getCurrentSemester();
	     List<Semester> semesters = semService.getSemestersForStudent(student);

	     // Fetch professor and handle if not found
	     String email = userDetails.getUsername();
	     Professor prof = profRepo.findByEmail(email)
	             .orElse(null);
	     
	     if (prof == null) {
	         return "redirect:/error"; // Redirect if professor not found
	     }

	     String role = prof.getRole();
	     String profImg = prof.getImg();
	     
	     if ("prof".equalsIgnoreCase(role)) {
	         role = "Professor";
	     }

	     // Add attributes to model
	     model.addAttribute("username", prof.getName());
	     model.addAttribute("email", prof.getEmail());
	     model.addAttribute("profId", prof.getProfId());
	     model.addAttribute("role", role);
	     model.addAttribute("profImg", profImg);
	     model.addAttribute("currentSemester", currentSemester);
	     model.addAttribute("student", student);
	     model.addAttribute("semesters", semesters);

	     return "professor/viewMentee";
	 }

	 @GetMapping("/mentee/attendance/{regNo}/{semNo}")
	 public ResponseEntity<List<Map<String, Object>>> getMenteeAttendance(
	         @PathVariable String regNo, @PathVariable String semNo) {

	     Long semesterNumber = Long.parseLong(semNo.replaceAll("[^0-9]", ""));
	     List<Report> reports = reportRepo.findByStudent_RegNoAndSemester_SemNo(regNo, semesterNumber);

	     List<Map<String, Object>> response = new ArrayList<>();

	     for (Report report : reports) {
	         int attendancePercentage = (report.getTotalPeriods() == 0) ? 0 :
	                 ((report.getPresentPeriods() + report.getOdPeriods()) * 100 / report.getTotalPeriods());

	         String color = (attendancePercentage >= 75) ? "green" :
	                        (attendancePercentage >= 50) ? "yellow" : "red";

	         Map<String, Object> subjectData = new HashMap<>();
	         subjectData.put("subjectId", report.getSubject().getSubId());
	         subjectData.put("subjectName", report.getSubject().getName());
	         subjectData.put("attendancePercentage", attendancePercentage);
	         subjectData.put("totalPeriods", report.getTotalPeriods());
	         subjectData.put("presentPeriods", report.getPresentPeriods());
	         subjectData.put("odPeriods", report.getOdPeriods());
	         subjectData.put("color", color);

	         response.add(subjectData);
	     }

	     return ResponseEntity.ok(response);
	 }
	 
	 @GetMapping("/mentee/attendance/total/{regNo}/{semNo}")
	 public ResponseEntity<Map<String, Object>> getTotalAttendance(
	         @PathVariable String regNo, @PathVariable String semNo) {

	     // Validate semNo
	     if (semNo == null || semNo.trim().isEmpty() || !semNo.matches("\\d+")) {
	         return ResponseEntity.badRequest()
	                 .body(Map.of("error", "Invalid semester number provided"));
	     }

	     // Convert to Long safely
	     Long semesterNumber = Long.parseLong(semNo);

	     // Fetch reports
	     List<Report> reports = reportRepo.findByStudent_RegNoAndSemester_SemNo(regNo, semesterNumber);

	     int totalPeriods = 0;
	     int totalPresentPeriods = 0;
	     int totalODPeriods = 0;
	     int totalAbsent=0;
	     int totalLate=0;

	     for (Report report : reports) {
	         totalPeriods += report.getTotalPeriods();
	         totalPresentPeriods += report.getPresentPeriods();
	         totalODPeriods += report.getOdPeriods();
	         totalAbsent+=report.getAbsentPeriods();
	         totalLate+=report.getLatePeriods();
	     }

	     double overallAttendancePercentage = (totalPeriods == 0) ? 0 :
	             ((double) (totalPresentPeriods + totalODPeriods) / totalPeriods) * 100;

	     Map<String, Object> response = new HashMap<>();
	     response.put("totalAttendancePercentage", overallAttendancePercentage);
	     response.put("totalPeriods", totalPeriods);
	     response.put("totalPresentPeriods", totalPresentPeriods);
	     response.put("totalODPeriods", totalODPeriods);
	     response.put("totalLate", totalLate);
	     response.put("totalAbsent", totalAbsent);

	     return ResponseEntity.ok(response);
	 }
	 
	 @GetMapping("/holidays/date-name")
	 @ResponseBody
	 public List<Map<String, String>> getHolidayDateNames() {
	     return holRepo.findAll().stream()
	         .map(h -> {
	             Map<String, String> map = new HashMap<>();
	             map.put("date", h.getHolidayDate().toString());
	             map.put("name", h.getHolidayName());
	             return map;
	         })
	         .toList();
	 }

	 @GetMapping("/updateProfessor")
		public String updateProfessor(Model model, @AuthenticationPrincipal UserDetails userDetails) {
			String email=userDetails.getUsername();
			Optional<Professor>optProfessor=profRepo.findByEmail(email);
			Professor prof=optProfessor.get();
			String role=prof.getRole();	
			String profImg=prof.getImg();
			if("prof".equalsIgnoreCase(role)) {
				role="Professor";			
			}
		    model.addAttribute("username",prof.getName());
		    model.addAttribute("email",prof.getEmail());
		    model.addAttribute("profId",prof.getProfId());
		    model.addAttribute("role",role);
		    model.addAttribute("profImg",profImg);
		    model.addAttribute("professor",prof);
			System.out.println("Update  is running");
			return "professor/updateProfessor";
		}

	 /*@PostMapping("/updateProfessor")
	 @ResponseBody
	 public ResponseEntity<String> updateProfessor(
	         @RequestParam("email") String email,
	         @RequestParam("phone") Long phone,
	         @RequestParam("age") Long age,
	         @RequestParam(value = "image", required = false) MultipartFile image,
	         @AuthenticationPrincipal UserDetails userDetails
	 ) {
	     String currentEmail = userDetails.getUsername();
	     Optional<Professor> optionalProfessor = profRepo.findByEmail(currentEmail);

	     if (optionalProfessor.isEmpty()) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Professor not found");
	     }

	     Professor professor = optionalProfessor.get();

	     professor.setEmail(email);
	     professor.setPhone(phone);
	     professor.setAge(age);

	     // Handle image if uploaded
	     if (image != null && !image.isEmpty()) {
	         try {
	             String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
	             Path uploadPath = Paths.get("uploads/profimg");
	             if (!Files.exists(uploadPath)) {
	                 Files.createDirectories(uploadPath);
	             }
	             Path filePath = uploadPath.resolve(fileName);
	             Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	             // Save the accessible path (URL path)
	             professor.setImg("/uploads/profimg/" + fileName);
	         } catch (IOException e) {
	             e.printStackTrace();
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
	         }
	     }

	     profRepo.save(professor);
	     return ResponseEntity.ok("Updated successfully");
	 }*/
	 @PostMapping("/updateProfessor")
	 @ResponseBody
	 public ResponseEntity<Map<String, String>> updateProfessor(
	        // @RequestParam("email") String email,
	         @RequestParam("phone") Long phone,
	         @RequestParam("age") Long age,
	         @RequestParam(value = "image", required = false) MultipartFile image,
	         @AuthenticationPrincipal UserDetails userDetails
	 ) {
	     String currentEmail = userDetails.getUsername();
	     Optional<Professor> optionalProfessor = profRepo.findByEmail(currentEmail);

	     if (optionalProfessor.isEmpty()) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                              .body(Map.of("error", "Professor not found"));
	     }

	     Professor professor = optionalProfessor.get();

	     //professor.setEmail(email);
	     professor.setPhone(phone);
	     professor.setAge(age);

	     String imageUrl = professor.getImg(); // Default to current image

	     if (image != null && !image.isEmpty()) {
	         try {
	             String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
	             Path uploadPath = Paths.get("uploads/profimg");
	             if (!Files.exists(uploadPath)) {
	                 Files.createDirectories(uploadPath);
	             }
	             Path filePath = uploadPath.resolve(fileName);
	             Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	             // Save the accessible path
	             imageUrl = "/uploads/profimg/" + fileName;
	             professor.setImg(imageUrl);
	         } catch (IOException e) {
	             e.printStackTrace();
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                  .body(Map.of("error", "Failed to upload image"));
	         }
	     }

	     profRepo.save(professor);

	     Map<String, String> response = new HashMap<>();
	     response.put("message", "Profile updated successfully");
	     response.put("imageUrl", imageUrl);

	     return ResponseEntity.ok(response);
	 }





	 
}
