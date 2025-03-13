package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ProfessorAddDTO;
import com.example.demo.dto.ProfessorResponseDTO;
import com.example.demo.dto.ProfessorUpdateDTO;
import com.example.demo.enums.ApprovalStatus;
import com.example.demo.model.Department;
import com.example.demo.model.Professor;
import com.example.demo.repo.DepartmentRepository;
import com.example.demo.repo.ProfessorRepository;

@Service
public class ProfessorService {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private ProfessorRepository profRepo;
	@Autowired
	private DepartmentRepository deptRepo;
	public String addProfessor(ProfessorAddDTO profDTO ) {
		System.out.println("password is: "+profDTO.getPassword());
		 String encryptedPassword = passwordEncoder.encode(profDTO.getPassword());
		 profDTO.setPassword(encryptedPassword);
		 System.out.println("Encrepted passoword is: "+encryptedPassword);
		if(profDTO.getDeptId()==null) {
			return "Error: Department ID must required";
		}
		Optional<Department>deptOptional=deptRepo.findById(profDTO.getDeptId());
		if(deptOptional.isEmpty()) {
			return "Error: Department not found";
		}
		Department dept=deptOptional.get();
		Professor existingHod = dept.getHod();
		if ("HOD".equalsIgnoreCase(profDTO.getRole()) && existingHod != null) {
			return "Error: This department already has an HOD assigned: " + existingHod.getName();
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
		prof.setPwd(encryptedPassword);
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
	public ProfessorResponseDTO getProfessorById(String profId) {
		return profRepo.findById(profId)
				.map(professor->new ProfessorResponseDTO(
						professor.getProfId(),
						professor.getName(),
						professor.getEmail(),
						professor.getRole(),
						professor.getDepartment())).orElse(null);
	}
	public String updateProfessor(String profId, ProfessorUpdateDTO updateProfessor) {
	    Optional<Professor> optionalProfessor = profRepo.findById(profId);
	    
	    if (optionalProfessor.isEmpty()) {
	        return "Error: Professor not found";
	    }
	    
	    Professor prof = optionalProfessor.get();
	    
	   
	    Department dept = deptRepo.findById(updateProfessor.getDeptId())
	        .orElseThrow(() -> new RuntimeException("Department not found"));

	   
	    if ("HOD".equals(updateProfessor.getRole())) {
	        Professor existingHod = dept.getHod();
	        
	        if (existingHod != null && !existingHod.getProfId().equals(profId)) {
	            return "Error: This department already has an HOD assigned: " + existingHod.getName();
	        }
	    }

	   
	    prof.setName(updateProfessor.getName());
	    prof.setEmail(updateProfessor.getEmail());
	    prof.setRole(updateProfessor.getRole());
	    prof.setDepartment(dept);
	    
	    profRepo.save(prof);
	    return "Professor updated successfully";
	}

//	public String updateProfessor(String profId,ProfessorUpdateDTO updateProfessor) {
//		Optional<Professor>optionalProfessor=profRepo.findById(profId);
//		if(optionalProfessor.isPresent()) {
//			Professor prof=optionalProfessor.get();
//			Department dept=deptRepo.findById(updateProfessor.getDeptId()).orElseThrow(() -> new RuntimeException("Department not found"));
//			Professor existingHod=dept.getHod();
//			if("HOD".equalsIgnoreCase(updateProfessor.getRole())&&existingHod!=null && !existingHod.getProfId().equals(profId)) {
//				return "Error: This department already has HOD assigned: "+existingHod.getName();
//			}
//			prof.setName(updateProfessor.getName());
//			prof.setEmail(updateProfessor.getEmail());
//			prof.setRole(updateProfessor.getRole());
//			
//			prof.setDepartment(dept);
//			profRepo.save(prof);
//			return "Professor updated successfully";			
//		}
//		return "Error: Professor not found";
//		
//	}
	public void deleteProfessor(String profId) {
		Professor prof=profRepo.findByProfId(profId).orElseThrow(() -> new RuntimeException("Professor not found!"));
		profRepo.delete(prof);
	}

}
