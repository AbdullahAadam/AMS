package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.DepartmentResponseDTO;
import com.example.demo.dto.DepartmentUpdateDTO;
import com.example.demo.dto.DeptResponseProfDTO;
import com.example.demo.model.Department;
import com.example.demo.model.Professor;
import com.example.demo.repo.DepartmentRepository;
import com.example.demo.repo.ProfessorRepository;

@Service
public class DepartmentService {
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private ProfessorRepository profRepo;
	public String addDepartment(Department dept) {
		Optional<Department>existingCode=deptRepo.findByCode(dept.getCode());
		if(existingCode.isPresent()) {
			return"Error: Department already exists";
		}
		deptRepo.save(dept);
		return"Department created successfully";
	}
	//Getting all Department
	public List<Department> getAllDepartments(){
		return deptRepo.findAll();
	}
	public DepartmentResponseDTO getDepartmentById(String deptId) {
	    return deptRepo.findById(deptId)
	        .map(department -> {
	            Professor hod = department.getHod();
	            List<DeptResponseProfDTO>professorDTOS=department.getProfessors().stream()
	            		.map(prof->new DeptResponseProfDTO(prof.getProfId(),prof.getName()))
	            		.collect(Collectors.toList());
	            return new DepartmentResponseDTO(
	            	department.getDeptId(),
	                department.getDeptName(),
	                hod != null ? hod.getProfId():null,
	                hod !=null?hod.getName():null,	
	                department.getPeriod(),
	                department.getYear(),
	                department.getCode(),
	                professorDTOS
	            );
	        }).orElse(null);
	}
	public String updateDepartment(String deptId, DepartmentUpdateDTO update) {
	    Department dept = deptRepo.findById(deptId)
	            .orElseThrow(() -> new RuntimeException("Department not found"));

	    
	    dept.setDeptName(update.getName());
	    dept.setPeriod(update.getPeriod());
	    dept.setYear(update.getYear());

	   
	    if (update.getHod() != null && !update.getHod().isEmpty()) {
	        Professor newHod = profRepo.findById(update.getHod())
	                .orElseThrow(() -> new RuntimeException("Professor not found"));

	        
	        boolean isAlreadyHod = deptRepo.isHodAlreadyAssigned(update.getHod(), deptId);
	        if (isAlreadyHod) {
	            throw new RuntimeException("Professor is already assigned as HOD in another department!");
	        }

	      
	        Professor existingHod = dept.getHod();
	        if (existingHod != null && !existingHod.getProfId().equals(newHod.getProfId())) {
	            throw new RuntimeException("This department already has an HOD assigned: " + existingHod.getName());
	        }

	       
	        newHod.setRole("HOD");
	        profRepo.save(newHod);
	    }

	    deptRepo.save(dept);
	    return "Department updated successfully";
	}
	public Long getTotalPeriod(String deptId) {
		Long period=deptRepo.getTotalPeriodByDepartmentId(deptId);
		Long prof=period-1;
		return prof;
	}


}
