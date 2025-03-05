package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ProfessorAddDTO;
import com.example.demo.enums.ApprovalStatus;
import com.example.demo.model.Department;
import com.example.demo.model.Professor;
import com.example.demo.repo.DepartmentRepository;
import com.example.demo.repo.ProfessorRepository;

@Service
public class ProfessorService {
	@Autowired
	private ProfessorRepository profRepo;
	@Autowired
	private DepartmentRepository deptRepo;
	public String addProfessor(ProfessorAddDTO profDTO ) {
		
		if(profDTO.getDeptId()==null) {
			return "Error: Department ID must required";
		}
		Optional<Department>deptOptional=deptRepo.findById(profDTO.getDeptId());
		if(deptOptional.isEmpty()) {
			return "Error: Department not found";
		}
		Optional<Professor>existingCode=profRepo.findByProfId(profDTO.getProfId());
		Optional<Professor>existingEmail=profRepo.findByEmail(profDTO.getEmail());
		if(existingCode.isPresent()) {
			return"Error: Professor already exists";
		}
		if(existingEmail.isPresent()) {
			return"Error: Email already exists";
		}
		Professor prof=new Professor();
		prof.setName(profDTO.getName());
		prof.setEmail(profDTO.getEmail());
		prof.setDepartment(deptOptional.get());
		prof.setRole(profDTO.getRole());
		prof.setProfId(profDTO.getProfId());
		prof.setApprovalStatus(ApprovalStatus.PENDING);
		prof.setAge(0L);
		prof.setPhone(0L);
		prof.setImg("");
		prof.setStudents(new ArrayList<>());
		prof.setSubjects(new ArrayList<>());
		prof.setPwd("");
		profRepo.save(prof);
		return "Professor Added Successfully";
		
		/*Optional<Professor>existingCode=profRepo.findByProfId(prof.getProfId());
		Optional<Professor>existingEmail=profRepo.findByEmail(prof.getEmail());
		Optional<Department>deptOptional=deptRepo.findById(prof.getDepartment().getDeptId());
		if(deptOptional.isEmpty()) {
			return "Error: Department not found";
		}
		if(existingCode.isPresent()) {
			return"Error: Professor already exists";
		}
		if(existingEmail.isPresent()) {
			return"Error: Email already exists";
		}
		prof.setDepartment(deptOptional.get());
		profRepo.save(prof);
		return"Professor created successfully";*/
	}
	public List<Professor> getApprovalByProfessorStatus(ApprovalStatus status){
		return profRepo.findByApprovalStatus(status);
		//List<Professor>professors=profRepo.findByApprovalStatus(status);
	}
	public List<Professor>getProfessorsByDepartmentId(String deptId,ApprovalStatus approvalStatus){
		return profRepo.findByDepartment_DeptIdAndApprovalStatus(deptId,ApprovalStatus.ACCEPTED);
	}
	public void acceptProfessor(String profId) {
		Professor prof=profRepo.findById(profId).orElseThrow(()->new RuntimeException("Professor not found"));
		prof.setApprovalStatus(ApprovalStatus.ACCEPTED);
		profRepo.save(prof);
	}
	public void rejectedProfessor(String profId) {
		Professor prof=profRepo.findById(profId).orElseThrow(()->new RuntimeException("Professor not found"));
		prof.setApprovalStatus(ApprovalStatus.REJECTED);
		profRepo.deleteById(profId);
	}

}
