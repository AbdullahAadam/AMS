package com.example.demo.controller;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.AttendanceCellDTO;
import com.example.demo.dto.SemesterAttendanceDTO;
import com.example.demo.model.Attendance;
import com.example.demo.model.Holiday;
import com.example.demo.model.Professor;
import com.example.demo.model.Report;
import com.example.demo.model.Semester;
//import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.model.Subject;
import com.example.demo.repo.AttendanceRepository;
import com.example.demo.repo.HolidayRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.ReportRepository;
import com.example.demo.repo.StudentRepository;
import com.example.demo.repo.SubjectRepository;
import com.example.demo.service.AttendanceService;
import com.example.demo.service.SemesterService;


@Controller
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	private StudentRepository studRepo;
	@Autowired
	private ProfessorRepository profRepo;
	@Autowired
	private SubjectRepository subRepo;
	@Autowired
	private AttendanceRepository attendanceRepo;
	@Autowired
	private ReportRepository reportRepo;
	@Autowired
	private HolidayRepository holidayRepo;
	
	@Autowired
	private AttendanceService attendanceService;
	@Autowired
	private SemesterService semService;
	@Autowired
	private PasswordEncoder passwordEncode;
	
	
	@PreAuthorize("hasAuthority('ROLE_STUDENT')")
	
	@GetMapping("/dashboard")
	public String dashboard(Model model,@AuthenticationPrincipal UserDetails userDetails) {
		String email=userDetails.getUsername();
		Optional<Student>optStudnet=studRepo.findByEmail(email);
		Student stud=optStudnet.get();
		String regNo=stud.getRegNo();
		String studImg=stud.getImg();
		Long totalPeriods=stud.getDepartment().getPeriod();
		/*ATTENDACNE CIRCLE
		
		LocalDate today = LocalDate.now();
		List<Attendance> todayAttendance = attendanceRepo
		        .findById_RegNoAndId_AttDate(regNo, today);
		Map<Long, String> periodStatus = new HashMap<>();
		for (Attendance att : todayAttendance) {
		     periodStatus.put(att.getId().getPeriod(), att.getStatus().name());
		}
		 // or fetch dynamically
		model.addAttribute("periodStatus", periodStatus);
		
		*/
		model.addAttribute("username",stud.getName());
		model.addAttribute("email",stud.getEmail());
		model.addAttribute("regNo",regNo);
		model.addAttribute("totalPeriods",totalPeriods);
		model.addAttribute("studImg",studImg);
		model.addAttribute("currentSem",stud.getCurrentSemester());
		return "student/dashboard";
	}
	@GetMapping("/attendance-status")
	@ResponseBody
	public Map<Long, String> getAttendanceStatus(@AuthenticationPrincipal UserDetails userDetails) {
	    String email = userDetails.getUsername();
	    Student student = studRepo.findByEmail(email).orElseThrow();
	    String regNo = student.getRegNo();
	    LocalDate today = LocalDate.now();

	    List<Attendance> todayAttendance = attendanceRepo.findById_RegNoAndId_AttDate(regNo, today);
	    Map<Long, String> periodStatus = new HashMap<>();
	    for (Attendance att : todayAttendance) {
	        periodStatus.put(att.getId().getPeriod(), att.getStatus().name());
	    }
	    return periodStatus;
	}
	@GetMapping("/attendance/total/")
	 public ResponseEntity<Map<String, Object>> getTotalAttendance(
	         @AuthenticationPrincipal UserDetails userDetails) {

	     // Validate semNo
	     
	     String email = userDetails.getUsername();
		 Student student = studRepo.findByEmail(email).orElseThrow();
		 String regNo=student.getRegNo();
		 String sem=student.getCurrentSemester();
		 Long semesterNumber; 
	     if("FINISHED".equalsIgnoreCase(sem)) {
	    	  semesterNumber=student.getDepartment().getSem();
	     }else {
	    	 semesterNumber = Long.parseLong(sem.replaceAll("[^0-9]", ""));
	     }
	     // Convert to Long safely
	    
	     LocalDate startDate = attendanceRepo.findFirstAttendanceDateBySemester(semesterNumber);
	     LocalDate endDate=attendanceRepo.findLastAttendanceDateBySemester(semesterNumber);
	     // Fetch reports
	     List<Report> reports = reportRepo.findByStudent_RegNoAndSemester_SemNo(regNo, semesterNumber);

	     int totalPeriods = 0;
	     int totalPresentPeriods = 0;
	     int totalODPeriods = 0;
	     
	     for (Report report : reports) {
	         totalPeriods += report.getTotalPeriods();
	         totalPresentPeriods += report.getPresentPeriods();
	         totalODPeriods += report.getOdPeriods();
	     }

	     double overallAttendancePercentage = (totalPeriods == 0) ? 0 :
	             ((double) (totalPresentPeriods + totalODPeriods) / totalPeriods) * 100;

	     Map<String, Object> response = new HashMap<>();
	     response.put("totalAttendancePercentage", overallAttendancePercentage);
	     response.put("totalPeriods", totalPeriods);
	     response.put("totalPresentPeriods", totalPresentPeriods);
	     response.put("totalODPeriods", totalODPeriods);
	     response.put("startDate", startDate);
	     response.put("endDate", endDate);
	     return ResponseEntity.ok(response);
	 }
	@GetMapping("/forgot")
	public String showForgot() {
		return "student/forgot";
	}
	@PostMapping("/forgot")
	public String processForgotPassword(@RequestParam String regNo,
										@RequestParam String email,
										RedirectAttributes redirectAttributes) {
		System.out.println("Enterd email: "+email);
		System.out.println("Entered RegNo: "+regNo);
		Optional<Student> optStudentRegNo=studRepo.findById(regNo);
		if(optStudentRegNo.isEmpty()) {
			redirectAttributes.addFlashAttribute("regNoError","Reg No does not exist");
			return "redirect:/student/forgot";
		}
		
		Optional<Student> optStudentEmail=studRepo.findByEmail(email);
		if(optStudentEmail.isEmpty()) {
			redirectAttributes.addFlashAttribute("emailError","Email does not Exit");
			return "redirect:/student/forgot";
		}
		
		redirectAttributes.addFlashAttribute("email",email);
		return"redirect:/student/reset";
		
	}
	@GetMapping("/reset")
	public String showResetPassword() {
		return "student/reset";
	}
	@PostMapping("/reset")
	public String processResetPassword(@RequestParam String email,
										@RequestParam String pwd,
										@RequestParam String cpwd,
										RedirectAttributes redirectAttributes) {
		
		Optional<Student> optStudentEmail=studRepo.findByEmail(email);
		if(optStudentEmail.isEmpty()) {
			redirectAttributes.addFlashAttribute("error","Please follow the correct Order");
			return "redirect:/student/forgot";
		}
		
		if(!cpwd.equals(pwd)) {
			redirectAttributes.addFlashAttribute("error","Passwords does not match");
			return "redirect:/student/reset";
		}		
		Student stud=optStudentEmail.get();
		stud.setPwd(passwordEncode.encode(pwd));
		studRepo.save(stud);
		redirectAttributes.addFlashAttribute("success","Password reset successfully");
		return "redirect:/student/login";
	}
	
	@GetMapping("/register")
	public String showRegisterForm() {
		return "student/register";
	}
	
	
	@GetMapping("/subjects")
	public String showSubjects(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		 
		String email = userDetails.getUsername();
		Student student = studRepo.findByEmail(email).orElseThrow();
		String regNo=student.getRegNo();
		
	

	     String currentSemester = student.getCurrentSemester();
	     List<Semester> semesters = semService.getSemestersForStudent(student);

	     // Fetch professor and handle if not found
	     
	     // Add attributes to model
	     model.addAttribute("regNo",regNo);
	     model.addAttribute("dept", student.getDepartment().getDeptName());
	     model.addAttribute("year",student.getCurrentYear());
	     model.addAttribute("name",student.getName());
	     model.addAttribute("currentSemester", currentSemester);
	     model.addAttribute("student", student);
	     model.addAttribute("semesters", semesters);
		
		return "student/paper";
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
	  @GetMapping("/upcoming")
	  @ResponseBody
	    public List<Holiday> getUpcomingHolidaysWithSundays() {
	        LocalDate today = LocalDate.now();
	        LocalDate endDate = today.plusDays(30); // Automatically calculate end date
	        
	        // 1. Get holidays from database within date range
	        List<Holiday> dbHolidays = holidayRepo.findByHolidayDateBetween(today, endDate);
	        
	        // 2. Add Sundays that aren't already holidays
	        List<Holiday> result = new ArrayList<>(dbHolidays);
	        today.datesUntil(endDate.plusDays(1)) // Include end date
	             .filter(date -> date.getDayOfWeek().getValue() == 7) // Sundays
	             .filter(date -> dbHolidays.stream().noneMatch(h -> h.getHolidayDate().equals(date)))
	             .forEach(date -> {
	                 Holiday sunday = new Holiday();
	                 sunday.setHolidayName("Sunday");
	                 sunday.setHolidayDate(date);
	                 sunday.setHolidayType("Weekly");
	                 result.add(sunday);
	             });
	        
	        // 3. Sort by date
	        result.sort((h1, h2) -> h1.getHolidayDate().compareTo(h2.getHolidayDate()));
	        
	        return result;
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
	 
	 @GetMapping("/viewAttendance")
		public String showViewAttendance(Model model, @AuthenticationPrincipal UserDetails userDetails) {
			 
			String email = userDetails.getUsername();
			Student student = studRepo.findByEmail(email).orElseThrow();
			String regNo=student.getRegNo();		
			String studImg=student.getImg();
			model.addAttribute("username",student.getName());
			model.addAttribute("email",student.getEmail());
			model.addAttribute("regNo",regNo);			
			model.addAttribute("studImg",studImg);
			
			return "student/viewAttendance";
	 }
	 
	 @GetMapping("/attendance/semesters/book/{regNo}")
	 @ResponseBody
	 public List<Integer> getSemestersWithAttendance(@PathVariable String regNo) {
	     return attendanceRepo.findDistinctSemestersByRegNo(regNo);
	 }
	 
	 @GetMapping("attendance/{regNo}/{sem}")
	    public ResponseEntity<SemesterAttendanceDTO> getAttendance(
	            @PathVariable String regNo,
	            @PathVariable Long sem) {
	        return ResponseEntity.ok(attendanceService.getAttendanceForSemester(regNo, sem));
	    }
	
	 
	 
	 @GetMapping("/attendance")
		public String showAttendance(Model model, @AuthenticationPrincipal UserDetails userDetails) {
			 
			String email = userDetails.getUsername();
			Student student = studRepo.findByEmail(email).orElseThrow();
			String regNo=student.getRegNo();			
		    String currentSemester = student.getCurrentSemester();
		    int semNo=1;
		    if("FINISHED".equalsIgnoreCase(currentSemester)) {
		    	semNo=student.getDepartment().getSem().intValue();
		    }else {
		    	 currentSemester = currentSemester.replaceAll("[^0-9]", "");
		    	 semNo=Integer.parseInt(currentSemester);
		    }
		     // Fetch professor and handle if not found
		     
		     // Add attributes to model
		     model.addAttribute("regNo",regNo);
		     //model.addAttribute("dept", student.getDepartment().getDeptName());
		    // model.addAttribute("year",student.getCurrentYear());
		     model.addAttribute("name",student.getName());
		     model.addAttribute("currentSemester", semNo);
		     model.addAttribute("username",student.getName());
			 model.addAttribute("email",student.getEmail());
			 model.addAttribute("studImg",student.getImg());
		    
			System.out.println("Current semester is: "+currentSemester);
			return "student/attendance";
		}
	 @GetMapping("/marked-months")
	 @ResponseBody
	 public List<String> getMarkedMonths(@AuthenticationPrincipal UserDetails userDetails,
	                                     @RequestParam("semester") Long semester) {
		 String email = userDetails.getUsername();
		 Student student = studRepo.findByEmail(email).orElseThrow();
		 String regNo=student.getRegNo();

	     List<Integer> monthNumbers = attendanceRepo.findDistinctMonths(regNo, semester);

	     // Convert month number to month name (e.g., 1 → JANUARY)
	     return monthNumbers.stream()
	                        .map(num -> Month.of(num).name()) // Returns enum name in UPPERCASE
	                        .toList();
	 }
	 @GetMapping("/month-attendance")
	 @ResponseBody
	 public Map<String, Object> getMonthAttendance(@AuthenticationPrincipal UserDetails userDetails,
	                                               @RequestParam("semester") Long semester,
	                                               @RequestParam("month") int month) {
	     String email = userDetails.getUsername();
	     Student student = studRepo.findByEmail(email).orElseThrow();
	     String regNo = student.getRegNo();
	     int hours = student.getDepartment().getPeriod().intValue();

	     List<Attendance> attendanceList = attendanceRepo.findByStudentSemesterMonth(regNo, semester, month);

	     Map<LocalDate, Map<Long, AttendanceCellDTO>> attendanceMap = new TreeMap<>();

	     for (Attendance a : attendanceList) {
	         LocalDate date = a.getId().getAttDate();
	         Long period = a.getId().getPeriod();
	         attendanceMap.putIfAbsent(date, new HashMap<>());

	         AttendanceCellDTO cell = new AttendanceCellDTO();
	         cell.setStatus(a.getStatus().toString());

	         // Subject
	         Subject subject = subRepo.findById(a.getId().getSubCode()).orElse(null);
	         if (subject != null) {
	             cell.setSubjectName(subject.getName());
	             cell.setSubjectCode(subject.getSubId());
	         }

	         // Marked by logic
	         String markedById = a.getMarkedByUser();
	         String markedByName = markedById;

	         if ("Professor".equalsIgnoreCase(a.getMarkedBy())) {
	             markedByName = profRepo.findByProfId(markedById)
	                                    .map(Professor::getName)
	                                    .orElse(markedById); // fallback if not found
	         } else if ("HOD".equalsIgnoreCase(a.getMarkedBy())) {
	             markedByName = profRepo.findById(markedById)
	                                    .map(hod -> "HOD " + hod.getName())
	                                    .orElse(markedById); // fallback if not found
	         }


	         cell.setMarkedBy(markedByName);
	         attendanceMap.get(date).put(period, cell);
	     }

	     Map<String, Object> response = new HashMap<>();
	     response.put("hours", hours);
	     response.put("data", attendanceMap);
	     return response;
	 }
	 
	 @GetMapping("/department")
		public String showDepartment(Model model, @AuthenticationPrincipal UserDetails userDetails) {
			 
			String email = userDetails.getUsername();
			Student student = studRepo.findByEmail(email).orElseThrow();
			String regNo=student.getRegNo();		
			String studImg=student.getImg();
			Professor mentor = student.getMentor();
			Professor hod = student.getDepartment().getHod();  
			model.addAttribute("username",student.getName());
			model.addAttribute("email",student.getEmail());
			model.addAttribute("regNo",regNo);			
			model.addAttribute("studImg",studImg);
			model.addAttribute("student", student);
			model.addAttribute("mentor", mentor);
			model.addAttribute("hod", hod);
			
			return "student/department";
	 }
	 
	 @GetMapping("/updateStudent")
		public String updateStudent(Model model, @AuthenticationPrincipal UserDetails userDetails) {
			String email=userDetails.getUsername();
			Optional<Student>optStudent=studRepo.findByEmail(email);
			Student stud=optStudent.get();
				
			String studImg=stud.getImg();
			
		    model.addAttribute("username",stud.getName());
		    model.addAttribute("email",stud.getEmail());
		    model.addAttribute("regNo",stud.getRegNo());
		   
		    model.addAttribute("studImg",studImg);
		    model.addAttribute("student",stud);
			System.out.println("Update  is running");
			return "student/update";
		}
	 @PostMapping("/updateStudent")
	 @ResponseBody
	 public ResponseEntity<Map<String, String>> updateStudent(
			 @RequestParam("name") String name,
			 //@RequestParam("email") String email,
			 @RequestParam("gender") String gender,
	         @RequestParam("phone") String phone,
	         @RequestParam("dob") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dob,
	         @RequestParam("age") int age,
	         @RequestParam("address") String address,
	         @RequestParam(value = "image", required = false) MultipartFile image,
	         @AuthenticationPrincipal UserDetails userDetails
	 ) {
	     String currentEmail = userDetails.getUsername();
	     Optional<Student> optionalStudent = studRepo.findByEmail(currentEmail);

	     if (optionalStudent.isEmpty()) {
	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                              .body(Map.of("error", "Student not found"));
	     }

	     Student student = optionalStudent.get();
	     student.setName(name);
	     //student.setEmail(email);
	     student.setGender(gender);
	     student.setPhone(phone);
	     student.setDob(dob);
	     student.setAge(age);
	     student.setAddress(address);
	     String imageUrl = student.getImg(); // Default to current image

	     if (image != null && !image.isEmpty()) {
	         try {
	             String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
	             Path uploadPath = Paths.get("uploads/studimg");
	             if (!Files.exists(uploadPath)) {
	                 Files.createDirectories(uploadPath);
	             }
	             Path filePath = uploadPath.resolve(fileName);
	             Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	             // Save the accessible path
	             imageUrl = "/uploads/studimg/" + fileName;
	             student.setImg(imageUrl);
	         } catch (IOException e) {
	             e.printStackTrace();
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                  .body(Map.of("error", "Failed to upload image"));
	         }
	     }

	     studRepo.save(student);

	     Map<String, String> response = new HashMap<>();
	     response.put("message", "Student updated successfully");
	     response.put("imageUrl", imageUrl);

	     return ResponseEntity.ok(response);
	 }



}
