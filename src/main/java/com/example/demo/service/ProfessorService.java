package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.ApprovalStatus;
import com.example.demo.model.Professor;
import com.example.demo.repo.ProfessorRepository;

@Service
public class ProfessorService {
	@Autowired
	private ProfessorRepository profRepo;
	public String addProfessor(Professor prof) {
		Optional<Professor>existingCode=profRepo.findByProfId(prof.getProfId());
		Optional<Professor>existingEmail=profRepo.findByEmail(prof.getEmail());
		if(existingCode.isPresent()) {
			return"Error: Professor already exists";
		}
		if(existingEmail.isPresent()) {
			return"Error: Email already exists";
		}
		profRepo.save(prof);
		return"Professor created successfully";
	}
	public List<Professor> getApprovalByProfessorStatus(ApprovalStatus status){
		return profRepo.findByApprovalStatus(status);
		//List<Professor>professors=profRepo.findByApprovalStatus(status);
		//return professors.stream().map(this::mapToDTO).collect(Collectors.toList());
	}
	/*private ProfessorDTO mapToDTO(Professor professor) {
		 return new ProfessorDTO(
	                professor.getProfId(),
	                professor.getEmail(),
	                professor.getApprovalStatus(),
	                professor.getName(),
	                professor.getRole(),
	                professor.getPwd(),
	                professor.getImg(),
	                professor.getPhone(),
	                professor.getAge(),
	                professor.getDepartment().getDeptId(), // Fetch department ID
	                professor.getDepartment().getDeptName(), // Fetch department name
	                professor.getDepartment().getCode(), // Fetch department code
	                professor.getDepartment().getYear(),
	                professor.getDepartment().getPeriod(),
	                professor.getDepartment().getSem(),
	                professor.getDepartment().getCreatedAt()
	        );
	}*/
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
