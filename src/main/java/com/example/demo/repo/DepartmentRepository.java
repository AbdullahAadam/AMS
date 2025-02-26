package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Department;
@Repository
public interface DepartmentRepository extends JpaRepository<Department,String>{
	Optional<Department>findByCode(String code);

}
