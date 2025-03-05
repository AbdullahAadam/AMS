package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,String>{
	List<Subject>findByDepartment_DeptId(String deptId);
	//boolean existsByProfessor_ProfIdAndSubId(String profId,String subId);

}
