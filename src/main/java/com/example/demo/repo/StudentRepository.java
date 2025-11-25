package com.example.demo.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.LogStatus;
import com.example.demo.enums.StudentStatus;
import com.example.demo.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student,String>{
	
	Optional<Student> findByEmail(String email);
	//boolean existByEmail(String email);
	
	Optional<Student>findByRegNo(String regNo);
	List<Student>findByLogStatus(LogStatus status);
	List<Student>findByStudentStatus(StudentStatus status);
	Long countByStudentStatus(StudentStatus status);
	//List<Student>findByApprovalStatus()
	
	 @Query("SELECT COUNT(s) FROM Student s WHERE s.mentor.profId = :professorId")
	 int countMenteesByProfessorId(@Param("professorId") String professorId);
	 
	 List<Student>findByMentorProfIdAndLogStatus(String profId,LogStatus status);
	 
	 List<Student>findByDepartmentDeptIdAndLogStatus(String deptId,LogStatus status);
	 
	 long countByMentorProfIdAndLogStatus(String profId,LogStatus status);
	 
	 long countByDepartmentDeptIdAndLogStatus(String deptId,LogStatus status);
	 
	 List<Student>findByMentorProfIdAndLogStatusAndStudentStatus(String profId,LogStatus status,StudentStatus studStatus);
	 
	 List<Student>findByDepartmentDeptIdAndLogStatusAndStudentStatus(String deptId,LogStatus status,StudentStatus studStatus);
	 
	 List<Student> findByMentorProfIdAndCurrentYearAndCurrentSemester(String profId, String currentYear, String currentSemester);

	 
	 List<Student> findByDepartmentDeptIdAndCurrentYearAndCurrentSemester(String deptId, String currentYear, String currentSemester);

	 List<Student> findByDepartmentDeptIdAndCurrentSemesterAndBatch(String deptId, String currentSemester, String batch);

	 
}
