	package com.example.demo.service;
	
	import java.text.ParseException;
	import java.text.SimpleDateFormat;
	import java.time.LocalDate;
	import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
	import java.util.Date;
	import java.util.List;
	import java.util.Optional;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.scheduling.annotation.Scheduled;
	import org.springframework.stereotype.Service;
	
	import com.example.demo.dto.AddStudentDTO;
	import com.example.demo.dto.ProfStudentResponseDTO;
	import com.example.demo.dto.ProfStudentUpdateDTO;
	import com.example.demo.dto.StudentResponseDTO;
	import com.example.demo.dto.StudentUpdateDTO;
	import com.example.demo.enums.LogStatus;
	import com.example.demo.enums.StudentStatus;
	import com.example.demo.model.Department;
	import com.example.demo.model.Professor;
	import com.example.demo.model.Semester;
	import com.example.demo.model.Student;
	import com.example.demo.repo.DepartmentRepository;
	import com.example.demo.repo.ProfessorRepository;
	import com.example.demo.repo.SemesterRepository;
	import com.example.demo.repo.StudentRepository;
	
	import jakarta.transaction.Transactional;
	
	@Service
	public class StudentService {
		@Autowired
		private StudentRepository studRepo;
		@Autowired
		private ProfessorRepository profRepo;
		@Autowired
		private DepartmentRepository deptRepo;
		@Autowired
		private SemesterRepository semRepo;
		
		@Scheduled(cron = "0 0 0 * * ?")  
	    @Transactional
	    public void updateAllStudentsStatus() {
	        List<Student> students = studRepo.findAll();	
	        for (Student student : students) {
	            updateCurrentYearAndSemester(student);
	        }
	        studRepo.saveAll(students);
	    }
		
		public String addStudent(AddStudentDTO studentDTO) {
			Optional<Student>existingRegNo=studRepo.findByRegNo(studentDTO.getRegNo());
			Optional<Student>existingEmail=studRepo.findByEmail(studentDTO.getEmail());
			if(existingRegNo.isPresent()) {
				return"Error: Student already exists. ";
			}
			if(existingEmail.isPresent()) {
				return"Error: Email already exists. ";
			}
			Department department = deptRepo.findById(studentDTO.getDeptId()).orElse(null);
		    if (department == null) {
		       return "Error: Invalid Department.";
		    }
		    Professor professor = profRepo.findById(studentDTO.getProfId()).orElse(null);
		    if (professor == null) {
		        return "Error: Invalid Professor.";
		    	}
			Student student = new Student();
	        student.setRegNo(studentDTO.getRegNo());
	        student.setName(studentDTO.getName());
	        student.setEmail(studentDTO.getEmail());
	       
	        student.setDepartment(department);
	        
	        student.setMentor(professor);
	        student.setLogStatus(LogStatus.PENDING);
	        student.setStudentStatus(StudentStatus.ACTIVE);
	        prepareStudentBeforeSave(student);
			studRepo.save(student);
			return "Student add Successfully";
		}
		public List<Student>getStudentByLogStatus(LogStatus status){
			return studRepo.findByLogStatus(status);
		}
		@Transactional
		public void acceptStudent(String regNo,String approvedBy,String role) {
			Student stud=studRepo.findById(regNo).orElseThrow(()->new RuntimeException("Student not found"));
			stud.setLogStatus(LogStatus.APPROVED);
			//stud.setStudentStatus(StudentStatus.ACTIVE);
			stud.setApprovedBy(approvedBy+"("+role+")");
			prepareStudentBeforeSave(stud);
			studRepo.save(stud);
		}
		public void rejectedStudent(String regNo) {
			Student stud=studRepo.findById(regNo).orElseThrow(()->new RuntimeException("Student not found"));
			stud.setLogStatus(LogStatus.REJECTED);
			studRepo.deleteById(regNo);
		}
		public StudentResponseDTO getStudentById(String regNo) {
		    return studRepo.findById(regNo)
		        .map(student -> {
		        	prepareStudentBeforeSave(student);// Ensure joinDate & current year/semester are updated
		        	studRepo.save(student);
		            return new StudentResponseDTO(
		                student.getRegNo(),
		                student.getName(),
		                student.getEmail(),
		                student.getDepartment().getDeptName(),
		                student.getDepartment().getDeptId(),
		                student.getMentor().getProfId(),
		                student.getMentor().getName()
		            );
		        }).orElse(null);
		}
		public ProfStudentResponseDTO getStudentsById(String regNo) {
		    return studRepo.findById(regNo)
		        .map(student -> {
		        	prepareStudentBeforeSave(student); // Ensure joinDate & current year/semester are updated
		        	studRepo.save(student);
		        	return new ProfStudentResponseDTO(
		                student.getRegNo(),
		                student.getDepartment().getDeptName(),
		                student.getName(),
		                student.getBatch(),
		                student.getCurrentYear(),
		                student.getJoinDate(),
		                student.getStudentStatus()
		            );
		        }).orElse(null);
		}
	
		public boolean updateStudent(String regNo,StudentUpdateDTO updateStudent) {
			Optional<Student>optionalStudent=studRepo.findById(regNo);
			if(optionalStudent.isPresent()) {
				Student stud=optionalStudent.get();
				stud.setName(updateStudent.getName());
				stud.setEmail(updateStudent.getEmail());
				Professor prof=profRepo.findByProfId(updateStudent.getProfId()).orElseThrow(() -> new RuntimeException("Professor not found"));
				stud.setMentor(prof);
				prepareStudentBeforeSave(stud);
				studRepo.save(stud);
				return true;
			}
			return false;
			
		}
		public boolean updateStudents(String regNo,ProfStudentUpdateDTO updateStudent) throws ParseException {
			Optional<Student>optionalStudent=studRepo.findById(regNo);
			if(optionalStudent.isPresent()) {
				Student stud=optionalStudent.get();
				System.out.println("???????????????");
				System.out.println("Old date: "+stud.getJoinDate());
				System.out.println("New date: "+updateStudent.getJoinDate());
				stud.setName(updateStudent.getName());
				stud.setBatch(updateStudent.getBatch());
				if (updateStudent.getJoinDate() != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String formattedDate = sdf.format(updateStudent.getJoinDate());
					Date parsedDate = sdf.parse(formattedDate);
		            stud.setJoinDate(parsedDate);
		        }
				stud.setStudentStatus(updateStudent.getStatus());
				stud.setCurrentYear(updateStudent.getYear());
				prepareStudentBeforeSave(stud);
				studRepo.save(stud);
				System.out.println("Upadated joinDate:>>>>?????" + stud.getJoinDate());
	
				return true;
			}
			return false;
			
		}
		public void deleteStudent(String regNo) {
			Student stud=studRepo.findById(regNo).orElseThrow(() -> new RuntimeException("Student not found!"));
			studRepo.delete(stud);
		}
		public List<Student>getPendingMentees(String profId){
			return studRepo.findByMentorProfIdAndLogStatus(profId,LogStatus.PENDING);
		}
		public List<Student>getPendingDepartmentStudents(String deptId){
			return studRepo.findByDepartmentDeptIdAndLogStatus(deptId, LogStatus.PENDING);
		}
		public List<Student>getApprovelMentees(String profId){
			List<Student>students=studRepo.findByMentorProfIdAndLogStatusAndStudentStatus(profId,LogStatus.APPROVED,StudentStatus.ACTIVE);
			for (Student student : students) {
		        updateCurrentYearAndSemester(student); // 🔹 Ensure the year and semester are updated
		        studRepo.save(student);
		    }
			return students;
		}
			 
		public List<Student>getApprovelDepartmentStudents(String deptId){
			List<Student>students=studRepo.findByDepartmentDeptIdAndLogStatusAndStudentStatus(deptId, LogStatus.APPROVED,StudentStatus.ACTIVE);
			for (Student student : students) {
		        updateCurrentYearAndSemester(student);
		        studRepo.save(student);
		    }
			return students;
		}
		/*public void updateStudentDetails(Student student) {
	        if (student.getJoinDate() == null) {
	            student.setJoinDate(student.getDefaultJoinDateFromRegNo());
	        }
	        student.updateCurrentYearAndSemester();
	        studRepo.save(student);
	    }*/
		public Student prepareStudentBeforeSave(Student student) {
	        student.setCreatedAt(LocalDateTime.now());
	        if (student.getLogStatus() == null) {
	            student.setLogStatus(LogStatus.PENDING);
	        }
	        if (student.getLogStatus() != LogStatus.APPROVED && student.getLogStatus() != LogStatus.REJECTED) {
	            student.setStudentStatus(StudentStatus.ACTIVE);
	        }
	        	
	        if (student.getJoinDate() == null) {
	            student.setJoinDate(java.sql.Date.valueOf(getDefaultJoinDateFromRegNo(student.getRegNo())));
	        }
	        /*if (student.getJoinDate() == null || !(student.getJoinDate() instanceof java.sql.Date)) {
	            student.setJoinDate(java.sql.Date.valueOf(getDefaultJoinDateFromRegNo(student.getRegNo())));
	        } else if (student.getJoinDate() instanceof java.util.Date && !(student.getJoinDate() instanceof java.sql.Date)) {
	            student.setJoinDate(new java.sql.Date(student.getJoinDate().getTime())); // Convert java.util.Date to java.sql.Date
	        }*/
	
	
	        updateCurrentYearAndSemester(student);
	        return student;
	    }
	
	    private LocalDate getDefaultJoinDateFromRegNo(String regNo) {
	        if (regNo != null && regNo.length() >= 4) {
	            try {
	                int joinYear = Integer.parseInt(regNo.substring(0, 4));
	                Optional<Semester> semOne = semRepo.findById(1L);
	                if (semOne.isPresent()) {
	                    int startMonth = semOne.get().getStartMonth().intValue();
	                    return LocalDate.of(joinYear, startMonth, 1);
	                }
	            } catch (NumberFormatException e) {
	                e.printStackTrace();
	            }
	        }
	        return LocalDate.now();
	    }
	
	    public void updateCurrentYearAndSemester(Student student) {
	        if (student.getJoinDate() == null) {
	            System.out.println("Join date is NULL for student: " + student.getRegNo());
	            return;
	        }

	        // ✅ Convert Date to LocalDate
	        LocalDate joinDate;
	        if (student.getJoinDate() instanceof java.sql.Date) {
	            joinDate = ((java.sql.Date) student.getJoinDate()).toLocalDate();
	        } else {
	            joinDate = student.getJoinDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        }

	        // ✅ Convert long to int explicitly
	        int totalMonthsCompleted = (int) Period.between(joinDate, LocalDate.now()).toTotalMonths();
	        int yearsCompleted = totalMonthsCompleted / 12;
	        int remainingMonths = totalMonthsCompleted % 12;

	        // Get course duration from department
	        int courseDuration = (student.getDepartment() != null) ? student.getDepartment().getYear().intValue() : 4;

	        System.out.println("RegNo: " + student.getRegNo() +
	                ", Join Date: " + student.getJoinDate() +
	                ", Total Months Completed: " + totalMonthsCompleted +
	                ", Years Completed: " + yearsCompleted +
	                ", Remaining Months: " + remainingMonths);

	        // Case 1: Student has finished the course
	        if (yearsCompleted >= courseDuration) {
	            student.setCurrentYear("FINISHED");
	            student.setCurrentSemester("FINISHED");
	        } 
	        // Case 2: Student is in the Final Year
	        else if (yearsCompleted == courseDuration - 1 && remainingMonths >= 6) {
	            student.setCurrentYear("FINAL");
	            student.setCurrentSemester("Semester " + (courseDuration * 2));  // Last semester based on department
	        } 
	        // Case 3: Student is progressing normally
	        else {
	            student.setCurrentYear(convertToRoman(yearsCompleted + 1));
	            
	            // Dynamically calculate semester based on department course duration
	            int semesterNo = (yearsCompleted * 2) + ((remainingMonths >= 6) ? 2 : 1);
	            int totalSemesters = courseDuration * 2;  // Each year has 2 semesters
	            
	            if (semesterNo > totalSemesters) {
	                student.setCurrentSemester("Semester " + totalSemesters);
	            } else {
	                student.setCurrentSemester("Semester " + semesterNo);
	            }
	        }

	        updateStudentStatus(student, yearsCompleted, courseDuration);
	        studRepo.save(student);
	    }



	
	    private void updateStudentStatus(Student student, int yearsCompleted, int courseDuration) {
	    	if (student.getStudentStatus() == StudentStatus.SUSPENDED) {
	            return; // Ensuring suspended students are not updated
	        }
	    	if (yearsCompleted >=courseDuration) {
	            student.setStudentStatus(StudentStatus.INACTIVE); 
	        } else {
	            student.setStudentStatus(StudentStatus.ACTIVE);
	        }
	    }
	
	    private String convertToRoman(int number) {
	        return switch (number) {
	            case 1 -> "I";
	            case 2 -> "II";
	            case 3 -> "III";
	            case 4 -> "IV";
	            case 5 -> "V";
	            case 6 -> "VI";
	            case 7 -> "VII";
	            case 8 -> "IX	";
	            default -> "FINAL";
	        };
	    }
	    public String updateStudentYearAndSemester(String regNo) {
	        Student student = studRepo.findById(regNo)
	                .orElseThrow(() -> new RuntimeException("Student not found"));

	        updateCurrentYearAndSemester(student); // Updates year and semester
	        studRepo.save(student); // Save the updated student

	        return student.getCurrentYear(); // Return the updated year in Roman numerals
	    }

	}	
