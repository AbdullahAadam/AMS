package com.example.demo.controller;

import java.util.List;
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

import com.example.demo.dto.AssignProfessorDTO;
//import com.example.demo.dto.AssignProfessorDTO;
import com.example.demo.dto.ProfessorAddDTO;
import com.example.demo.dto.ProfessorDTO;
import com.example.demo.dto.SubjectAddDTO;
import com.example.demo.enums.ApprovalStatus;
import com.example.demo.enums.LogStatus;
import com.example.demo.model.Admin;
import com.example.demo.model.Department;
import com.example.demo.model.Holiday;
import com.example.demo.model.Professor;
import com.example.demo.model.Semester;
import com.example.demo.model.Student;
import com.example.demo.model.Subject;
import com.example.demo.repo.AdminRepository;
import com.example.demo.repo.DepartmentRepository;
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
	    Admin admin=adminOpt.get();
	    long deptCount=deptRepo.count();
	    long profCount=profRepo.count();
	    long studCount=studRepo.count();
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
	    model.addAttribute("deptCount",deptCount);
	    model.addAttribute("profCount",profCount);
	    model.addAttribute("studCount",studCount);
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
	    model.addAttribute("username",admin.getName());
	    model.addAttribute("email",admin.getEmail());
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
	    List<Subject> subjects=subRepo.findAll();
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
	@PostMapping("/addSemester")
	public ResponseEntity<String>addSemester(@RequestBody Semester sem){
		String result=semService.addSemester(sem);
		if(result.startsWith("Error")) {
			return ResponseEntity.status(409).body(result);
		}else {
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
	@GetMapping("/listDepartments")
	@ResponseBody
    public List<Department> getDepartments() {
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
	@PostMapping("/addStudent")
	public ResponseEntity<String>addStudent(@RequestBody Student stud){
		String result=studService.addStudent(stud);
		if(result.startsWith("Error")) {
			return ResponseEntity.status(409).body(result);
		}else {
			return ResponseEntity.status(201).body(result);
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
		return allProfessors.stream().filter(prof->prof.getProfId().startsWith("PROF-")).collect(Collectors.toList());
	}
	@GetMapping("/subjects/{deptId}")
	@ResponseBody
	public List<Subject>getSubjectByDepartmentId(@PathVariable String deptId){
		List<Subject>allSubject=subService.getSubjectByDepartmentId(deptId);
		return allSubject;
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
	public ResponseEntity<?> acceptStudent(@PathVariable String regNo) {
	    try {
	    	studService.acceptStudent(regNo);
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
	

}
