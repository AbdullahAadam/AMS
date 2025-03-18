package com.example.demo.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.ApprovalStatus;
import com.example.demo.model.Professor;


@Repository
public interface ProfessorRepository extends JpaRepository<Professor,String>{
	
	Optional<Professor> findByEmail(String email);
	//boolean existByEmail(String email);
	Optional<Professor>findByProfId(String profId);
	List<Professor>findByApprovalStatus(ApprovalStatus stauts);
	List<Professor>findByDepartment_DeptIdAndApprovalStatus(String deptId,ApprovalStatus approvalStatus);
	List<Professor>findByRole(String role);
	
	@Query("SELECT COUNT(DISTINCT s.department) FROM Subject s JOIN s.professors p WHERE p.profId = :profId")
    Long countProfessorDepartments(@Param("profId") String profId);
	 
	 Long countByDepartmentDeptId(String deptId);
}
