package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Department;
import com.example.demo.repo.DepartmentRepository;

@Service
public class DepartmentService {
	@Autowired
	private DepartmentRepository departmentRepo;
	public String addDepartment(Department dept) {
		Optional<Department>existingCode=departmentRepo.findByCode(dept.getCode());
		if(existingCode.isPresent()) {
			return"Error: Department already exists";
		}
		departmentRepo.save(dept);
		return"Department created successfully";
	}
	//Getting all Department
	public List<Department> getAllDepartments(){
		return departmentRepo.findAll();
	}

}
