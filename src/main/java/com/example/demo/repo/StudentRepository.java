package com.example.demo.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.enums.LogStatus;
import com.example.demo.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student,String>{
	
	Optional<Student> findByEmail(String email);
	//boolean existByEmail(String email);
	
	Optional<Student>findByRegNo(String regNo);
	List<Student>findByLogStatus(LogStatus status);
	
}
