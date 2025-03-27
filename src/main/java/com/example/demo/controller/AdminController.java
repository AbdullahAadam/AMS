package com.example.demo.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
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
//import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.AddSemDTO;
import com.example.demo.dto.AddStudentDTO;
import com.example.demo.dto.AssignProfessorDTO;
import com.example.demo.dto.AssignSubDTO;
import com.example.demo.dto.DepartmentResponseDTO;
import com.example.demo.dto.DepartmentUpdateDTO;
//import com.example.demo.dto.AssignProfessorDTO;
import com.example.demo.dto.ProfessorAddDTO;
import com.example.demo.dto.ProfessorDTO;
import com.example.demo.dto.ProfessorResponseDTO;
import com.example.demo.dto.ProfessorUpdateDTO;
import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.dto.StudentUpdateDTO;
import com.example.demo.dto.SubjectAddDTO;
import com.example.demo.dto.SubjectUpdateDTO;
import com.example.demo.enums.ApprovalStatus;
import com.example.demo.enums.LogStatus;
import com.example.demo.enums.StudentStatus;
import com.example.demo.model.Admin;
import com.example.demo.model.Department;
import com.example.demo.model.Holiday;
import com.example.demo.model.Professor;
import com.example.demo.model.Semester;
import com.example.demo.model.Student;
import com.example.demo.model.Subject;
import com.example.demo.repo.AdminRepository;
import com.example.demo.repo.DepartmentRepository;
import com.example.demo.repo.HolidayRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.SemesterRepository;
import com.example.demo.repo.StudentRepository;
import com.example.demo.repo.SubjectRepository;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.HolidayService;
import com.example.demo.service.ProfessorService;
import com.example.demo.service.SemesterService;
import com.example.demo.service.StudentService;
import com.example.demo.service.SubjectService;

@Controller
@RequestMapping("/admin")
public class AdminController {
//	@GetMapping("/dashboard")
//	public String dashboard(Model model,@AuthenticationPrincipal UserDetails userDetails){
//		model.addAttribute("username",userDetails.getUsername());
//		return "admin/dashboard";
//	}
	@Autowired
	private PasswordEncoder passwordEncode;
	@Autowired
	private AdminRepository adminRepo;
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private ProfessorRepository profRepo;
	@Autowired
	private StudentRepository studRepo;
	@Autowired
	private SubjectRepository subRepo;
	@Autowired
	private SemesterRepository semRepo;
	@Autowired
	private HolidayRepository holRepo;
	@Autowired
	private HolidayService holidayService;
	@Autowired
	private SemesterService semService;
	@Autowired
	private DepartmentService deptService;
	@Autowired
	private ProfessorService profService;
	@Autowired
	private StudentService studService;
	@Autowired
	private SubjectService subService;
	
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
	    List<Professor>pendingProfessors=profService.getApprovalByProfessorStatus(ApprovalStatus.PENDING);
	    List<Student>pendingStudents=studService.getStudentByLogStatus(LogStatus.PENDING);
	    //List<Student>aliveStud=studRepo.findByStudentStatus(StudentStatus.ACTIVE);
	    Admin admin=adminOpt.get();
	    long deptCount=deptRepo.countByIsActiveTrue();
	    long profCount=profRepo.count();
	    long studCount=studRepo.countByStudentStatus(StudentStatus.ACTIVE);
	    long subCount=subRepo.countByIsActiveTrue();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("deptCount",deptCount);
	    model.addAttribute("profCount",profCount);
	    model.addAttribute("subCount",subCount);
	    model.addAttribute("studCount",studCount);
	    model.addAttribute("subCount",subCount);
	    model.addAttribute("pendingProfessors",pendingProfessors);
	    model.addAttribute("pendingStudents",pendingStudents);
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
		
		Optional<Admin> optAdmin=adminRepo.findByEmail(email);
		if(optAdmin.isEmpty()) {
			redirectAttributes.addFlashAttribute("error","Please follow the correct Order");
			return "redirect:/admin/forgot";
		}
		
		if(!cpwd.equals(pwd)) {
			redirectAttributes.addFlashAttribute("error","Passwords does not match");
			return "redirect:/admin/reset";
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
	    List<Professor>professors=profRepo.findByApprovalStatus(ApprovalStatus.ACCEPTED);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("professors",professors);
		System.out.println(" Edit Professor is running");
		return "admin/editProfessor";
	}
	@GetMapping("/statusProfessor")
	public String statusProfessor(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    List<Professor>pendingProfessors=profService.getApprovalByProfessorStatus(ApprovalStatus.PENDING);
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("pendingProfessors",pendingProfessors);
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
	    List<Student>students=studRepo.findByLogStatus(LogStatus.APPROVED);
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("students",students);
		System.out.println(" Edit Student is running");
		return "admin/editStudent";
	}
	@GetMapping("/statusStudent")
	public String statusStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    List<Student>pendingStudents=studService.getStudentByLogStatus(LogStatus.PENDING);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("pendingStudents",pendingStudents);
		System.out.println(" Status Student is running");
		System.out.println(pendingStudents);
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
	    List<Department>departments=deptRepo.findActiveDepartments();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("departments",departments);
		System.out.println(" Edit Department is running");
		return "admin/editDepartment";
	}
	@GetMapping("/addSubject")
	public String addSubject(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    List<Long> semNumber=semRepo.findAll().stream().map(Semester::getSemNo).collect(Collectors.toList());
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("semNumber",semNumber);
	    model.addAttribute("toastrMessage","Subject ID create automatically you can change if you want");
		System.out.println(" Add Subject is running");
		return "admin/addSubject";
	}
	@GetMapping("/editSubject")
	public String editSubject(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    //List<Subject> subjects=subRepo.findAll();
	    List<Subject> subjects=subRepo.findActiveSubjects();
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("subjects",subjects);
		System.out.println(" Edit Subject is running");
		return "admin/editSubject";
	}
	@GetMapping("/assignProfessor")
	public String assignProfessor(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
	    Optional<Admin> adminOpt=adminRepo.findByEmail(email);
	    Admin admin=adminOpt.get();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
		System.out.println(" Edit Subject is running");
		return "admin/assignProfessor";
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
	@PostMapping("/addSemester")
	public ResponseEntity<String>addSemester(@RequestBody AddSemDTO semDTO){
		
		 Semester sem = new Semester();
		    sem.setSemNo(semDTO.getSemNo());
		    sem.setStartMonth(semDTO.getStartMonth());
		    sem.setEndMonth(semDTO.getEndMonth());

		    String result = semService.addSemester(sem);  // Pass the converted entity
		    System.out.println("Received Data:>>>>>>> " + semDTO);

		    if (result.startsWith("Error")) {
		        return ResponseEntity.status(409).body(result);
		    } else {
		        return ResponseEntity.status(201).body(result);
		    }
	}
	@PostMapping("/addDepartment")
	public ResponseEntity<String>addDepartment(@RequestBody Department dept){
		String result=deptService.addDepartment(dept);
		if(result.startsWith("Error")) {
			return ResponseEntity.status(409).body(result);
		}else {
			return ResponseEntity.status(201).body(result);
		}
		
	}
	@GetMapping("/department/{deptId}")
	public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable String deptId) {
		DepartmentResponseDTO dept=deptService.getDepartmentById(deptId);
		if(dept!=null) {
			return ResponseEntity.ok(dept);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		
	}
	@GetMapping("/listDepartments")
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> getDepartments() {
	    List<Department> departments = deptService.getAllDepartments();
	    
	    // Create a response with only required fields
	    List<Map<String, Object>> response = new ArrayList<>();
	    for (Department dept : departments) {
	        Map<String, Object> deptData = new HashMap<>();
	        deptData.put("deptId", dept.getDeptId());
	        deptData.put("deptName", dept.getDeptName());
	        deptData.put("sem", dept.getSem());
	        deptData.put("code", dept.getCode());// Include only relevant semester info
	        response.add(deptData);
	    }
	    
	    return ResponseEntity.ok(response);
	}
	
    /*public List<Department> getDepartments() {
		List<Department>departments=deptService.getAllDepartments();
        System.out.println("Departments are: "+departments);
		return  departments;
    }
	/*public ResponseEntity<List<Department>> listDepartments() {
	    // Get all departments from the repository
	    List<Department> departments = deptRepo.findAll();

	    // Return the departments as JSON
	    return ResponseEntity.ok(departments);
	}*/
	@PostMapping("/addProfessor")
	public ResponseEntity<String>addProfessor(@RequestBody ProfessorAddDTO profDTO){
		String result=profService.addProfessor(profDTO);
		if(result.startsWith("Error")) {
			return ResponseEntity.status(409).body(result);
		}else {
			return ResponseEntity.status(201).body(result);
		}
		
	}
	@GetMapping("/professor/edit/{profId}")
	public ResponseEntity<ProfessorResponseDTO>getProfessorById(@PathVariable String profId){
		ProfessorResponseDTO prof=profService.getProfessorById(profId);
		if(prof!=null) {
			return ResponseEntity.ok(prof);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	@PutMapping("/professor/update/{profId}")
	public ResponseEntity<String> updateProfessor(@PathVariable String profId, @RequestBody ProfessorUpdateDTO updateProfessor) {
	    String responseMessage = profService.updateProfessor(profId, updateProfessor);
	    
	    if (responseMessage.startsWith("Error:")) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
	    } else {
	        return ResponseEntity.ok(responseMessage);
	    }
	}

	@DeleteMapping("/professor/delete/{profId}")
	public ResponseEntity<String>deleteProfessor(@PathVariable String profId){
		try {
			profService.deleteProfessor(profId);
			return ResponseEntity.ok("Professor Deleted successfully");
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Deleting in professor"+e.getMessage());
			
		}
	}
	@PostMapping("/addStudent")
	public ResponseEntity<String>addStudent(@RequestBody AddStudentDTO studentDTO){
		String result=studService.addStudent(studentDTO);
		if(result.startsWith("Error")) {
			return ResponseEntity.status(409).body(result);
		}else {
			return ResponseEntity.status(201).body(result);
		}
	}
	/*@PutMapping("/student/edit/{regNo}")
	public ResponseEntity<String>getStudentByReNo(@PathVariable String regNo,@RequestBody StudentUpdateDTO updateStudent){
		boolean result =studService.updateStudent(regNo, updateStudent);
		if(result) {
			return ResponseEntity.ok("Student updated successfully");
		}else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update Student.");
		}
	}*/
	@GetMapping("/student/edit/{regNo}")
	public ResponseEntity<StudentResponseDTO>getStudentByRegNo(@PathVariable String regNo){
		StudentResponseDTO stud=studService.getStudentById(regNo);
		if(stud!=null) {
			return ResponseEntity.ok(stud);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	@PutMapping("/student/update/{regNo}")
	public ResponseEntity<String>updateStudent(@PathVariable String regNo,@RequestBody StudentUpdateDTO updateStudent){
		boolean success=studService.updateStudent(regNo, updateStudent);
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
	@PostMapping("/addSubject")
	public ResponseEntity<String>addSubject(@RequestBody SubjectAddDTO subDTO){
		String result=subService.addSubject(subDTO);
		if(result.startsWith("Error")) {
			return ResponseEntity.status(409).body(result);
		}else {
			return ResponseEntity.status(201).body(result);
		}
		
	}
	@DeleteMapping("/department/delete/{deptId}")
	public ResponseEntity<String>softDeleteDepartment(@PathVariable String deptId){
		Optional<Department>deptOptional=deptRepo.findById(deptId);
		if(deptOptional.isPresent()) {
			Department dept=deptOptional.get();
			dept.setActive(false);
			deptRepo.save(dept);
			return ResponseEntity.ok("Department Deleted Successfully.");
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Department not found");
		}
	}
	@PutMapping("/department/update/{deptId}")
	public ResponseEntity<String>updateDepartment(@PathVariable String deptId, @RequestBody DepartmentUpdateDTO dto){
		try {
			String response=deptService.updateDepartment(deptId, dto);
			return ResponseEntity.ok(response);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PostMapping("/assignProfessor")
	public ResponseEntity<String>assignProfessor(@RequestBody AssignProfessorDTO profDTO){
		String result=subService.assignProfessorToSubject(profDTO);
		if(result.startsWith("Error")) {
			return ResponseEntity.status(409).body(result);
		}else {
			return ResponseEntity.status(201).body(result);
		}
	}
	/*@GetMapping("/getPendingProfessors")
	@ResponseBody
    public List<Professor> getPendingProfessors() {
        List<Professor>pendingProfessors=profService.getApprovalByProfessorStatus(ApprovalStatus.PENDING); 
		System.out.println("Pendingzzzzzzzz"+pendingProfessors);
        return pendingProfessors;
    }*/
	@GetMapping("/professors/{deptId}")
	@ResponseBody
	public List<Professor>getProfessorsByDepartmentId(@PathVariable String deptId){
		//List<Professor>getProfessorsByDepartmentId
		List<Professor> allProfessors= profService.getProfessorsByDepartmentId(deptId,ApprovalStatus.ACCEPTED);
		//return allProfessors.stream().filter(prof->prof.getProfId().startsWith("PROF-")).collect(Collectors.toList());
		return allProfessors.stream().filter(prof->"prof".equalsIgnoreCase(prof.getRole())).collect(Collectors.toList());
	}	
	@GetMapping("/subjects/{deptId}")
	@ResponseBody
	public List<AssignSubDTO> getSubjectsByDepartment(@PathVariable String deptId) {
	    List<Subject> subjects = subService.getSubjectByDepartmentId(deptId);
	    System.out.println("SubjectsZZZZZZZ"+subjects);
	    return subjects.stream().map(AssignSubDTO::new).collect(Collectors.toList());
	}

	
	@PutMapping("/acceptProfessor/{profId}")
	/*public String acceptProfessor(@PathVariable String profId) {
		profService.acceptProfessor(profId);
		return "Accepted";
	}*/
	public ResponseEntity<?> acceptProfessor(@PathVariable String profId) {
	    try {
	        profService.acceptProfessor(profId);
	        return ResponseEntity.ok("Professor accepted successfully.");
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Error accepting professor: " + ex.getMessage());
	    }
	}
	@DeleteMapping("/rejectProfessor/{profId}")
	public ResponseEntity<?> rejectProfessor(@PathVariable String profId) {
	    try {
	        profService.rejectedProfessor(profId);
	        return ResponseEntity.ok("Professor rejected successfully.");
	    } catch (Exception ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                             .body("Error rejected professor: " + ex.getMessage());
	    }
	}
	/*public String rejectProfessor(@PathVariable String profId) {
		profService.rejectedProfessor(profId);
		String message="Rejected";
		return message;
	}*/
	@PutMapping("/acceptStudent/{regNo}")
	public ResponseEntity<?> acceptStudent(@PathVariable String regNo,@AuthenticationPrincipal UserDetails userDetails) {
	    try {
	    	String email=userDetails.getUsername();
		    Optional<Admin> adminOpt=adminRepo.findByEmail(email);		    
		    if (adminOpt == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Approver not found");
	        }
		    Admin admin=adminOpt.get();
		    String name=admin.getName();
		    String role="Admin";
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
	@GetMapping("/subjects/{subId}/professors")
	@ResponseBody
	public ResponseEntity<List<ProfessorDTO>>getAssignedProfessors(@PathVariable String subId){
		Subject sub=subRepo.findById(subId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Subject not found"));
		List<ProfessorDTO> professorsDTO = sub.getProfessors().stream()
                  .filter(prof -> prof.getProfId() != null && prof.getName() != null)
                  .map(prof->new ProfessorDTO(prof.getProfId(),prof.getName(),prof.getEmail(),(prof.getDepartment()!=null)?prof.getDepartment().getCode():"Unknown"))
                  .collect(Collectors.toList());
		return ResponseEntity.ok(professorsDTO);
	}
	
	/*
	 * for one way sub->prof;
	 * public List<Professor>getAssignedProfessors(@PathVariable String subId){
		Subject sub=subRepo.findById(subId)
				.orElseThrow(()->new RuntimeException("Subject not found"));
		
=
		return sub.getProfessors(); 
	}*/
	/*@GetMapping("/subjects/{subId}/professors")
	public ResponseEntity<List<Map<String,String>>>getAssignedProfessor(@PathVariable String subId){
		Optional<Subject>subject=subRepo.findById(subId);
		if(subject.isPresent()) {
			List<Map<String,String>>professorData=subject.get().getProfessor().stream()
					.map(prof->{
						Map<String,String>map=new HashMap<>();
						map.put("profId",prof.getProfId());
						map.put("name",prof.getName());
						return map;
					})
					.collect(Collectors.toList());
			return ResponseEntity.ok(professorData);
		}
		return ResponseEntity.notFound().build();
	}*/
	@DeleteMapping("/subjects/{subId}/removeProfessor/{profId}")
	public ResponseEntity<String>removeProfessorFromSubject(@PathVariable String subId,@PathVariable String profId){
		Subject sub=subRepo.findById(subId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
		Professor prof=profRepo.findById(profId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor not found"));
		if(sub.getProfessors().contains(prof)) {
			sub.getProfessors().remove(prof);
			subRepo.save(sub);
			return ResponseEntity.ok("Professor removed successfully");
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professor not assigned this to subjects");
	}
	@GetMapping("/{subId}")
	public ResponseEntity<?> getSubjectById(@PathVariable String subId) {
	    Subject subject = subService.getSubject(subId);
	    if (subject == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subject not found");
	    }

	    Semester semester = subject.getSemester(); // ✅ Fetch the semester object

	    // Construct response with semester details
	    Map<String, Object> response = new HashMap<>();
	    response.put("subId", subject.getSubId());
	    response.put("name", subject.getName());
	    response.put("type", subject.getType());
	    response.put("department", subject.getDepartment());
	    response.put("professors", subject.getProfessors());
	    response.put("active", subject.isActive());
	    response.put("createdAt", subject.getCreatedAt());

	    // Include semester details if available
	    if (semester != null) {
	        Map<String, Object> semesterDetails = new HashMap<>();
	        semesterDetails.put("semNo", semester.getSemNo()); // ✅ Add semester number
	        semesterDetails.put("startMonth", semester.getStartMonth());
	        semesterDetails.put("endMonth", semester.getEndMonth());
	        response.put("semester", semesterDetails);
	    } else {
	        response.put("semester", null);
	    }

	    return ResponseEntity.ok(response);
	}
/*@GetMapping("/admin/subjects/{id}")
	public ResponseEntity<?> getSubject(@PathVariable String id) {
	    return subRepo.findById(id)
	        .map(subject -> ResponseEntity.ok(subject))  
	        .orElseThrow(); 
	}*/


	@PutMapping("/update/subjects/{subId}")
	public ResponseEntity<String>updateSubject(@PathVariable String subId,@RequestBody SubjectUpdateDTO request){
		Subject sub=subRepo.findById(subId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
		sub.setName(request.getName());
		sub.setType(request.getType());
		//sub.setSemester(request.getSemester());
		//sub.setSemester().get
		//Department dept=deptRepo
		Semester sem=semRepo.findById(request.getSemNo()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Semester not found"));
		sub.setSemester(sem);
		List<String>professorIds=request.getProfessorIds();
		if(professorIds ==null) {
			professorIds=new ArrayList<>();
		}
		if(!professorIds.isEmpty()) {
			List<Professor>newProfessors=profRepo.findAllById(professorIds);
			if(newProfessors.size()!=professorIds.size()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some professors were not found");
			}
			sub.setProfessors(new ArrayList<>(newProfessors));
		}else {
			sub.getProfessors().clear();
		}
		subRepo.save(sub);
		return ResponseEntity.ok("Subject updated successfully");
		
	}
	@PostMapping("/subjects/delete/{subId}")
	public ResponseEntity<String>softDeleteSubject(@PathVariable String subId){
		Optional<Subject>subOptional=subRepo.findById(subId);
		if(subOptional.isPresent()) {
			Subject sub=subOptional.get();
			sub.setActive(false);
			subRepo.save(sub);
			return ResponseEntity.ok("Subject Deleted Successfully.");
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subject not found");
		}
	}
	

}
