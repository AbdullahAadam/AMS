package com.example.demo.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.dto.AssignProfessorDTO;
import com.example.demo.dto.AttendSubjectDTO;
import com.example.demo.dto.SubjectAddDTO;
import com.example.demo.model.Department;
import com.example.demo.model.Professor;
import com.example.demo.model.Semester;
import com.example.demo.model.Subject;
import com.example.demo.repo.DepartmentRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.SemesterRepository;
import com.example.demo.repo.SubjectRepository;

import jakarta.transaction.Transactional;

@Service
public class SubjectService {
	@Autowired
	private SubjectRepository subRepo;
	@Autowired
	private DepartmentRepository deptRepo;
	@Autowired
	private SemesterRepository semRepo;
	@Autowired
	private ProfessorRepository profRepo;
	
	public Subject getSubject(@PathVariable String subId) {
		return subRepo.findById(subId).orElse(null);
	}
	public String addSubject(SubjectAddDTO subDTO) {
		if(subDTO.getSubId()==null) {
			return "Error: Subject Id must required";
		}
		Optional<Subject>existingSubId=subRepo.findById(subDTO.getSubId());
		if(existingSubId.isPresent()) {
			return "Error: Subject Id already exists";
		}
		Optional<Department>deptOptional=deptRepo.findById(subDTO.getDeptId());
		if(deptOptional.isEmpty()) {
			return "Error: Department not found";
		}
		Optional<Semester>semOptional=semRepo.findById(subDTO.getSemNo());
		if(semOptional.isEmpty()) {
			return "Error: Semester Number not found";
		}
		Subject sub=new Subject();
		String subType=subDTO.getType();
		//System.out.println(subType);
		String type=null;
		if("C-".equals(subType)) {
			type="Core";
		}else if("E-".equals(subType)) {
			type="Elective";
		}else if("EX-".equals(subType)) {
			type="Extra Paper";
		}else {
			return "Error: Invalid Subject Type";
		}
		sub.setSubId(subDTO.getSubId());
		sub.setName(subDTO.getName());
		sub.setType(type);
		sub.setDepartment(deptOptional.get());
		sub.setSemester(semOptional.get());
		sub.setProfessors(null);
		subRepo.save(sub);
		return "Subject Added Successfully";
		
	}
	public List<Subject>getSubjectByDepartmentId(String deptId){
		return subRepo.findByDepartment_DeptId(deptId);
	}
	/*public String assignProfessorToSubject(AssignProfessorDTO profDTO) {
		Optional<Subject>optSubject=subRepo.findById(profDTO.getSubId());
		Optional<Professor>optProfessor=profRepo.findByProfId(profDTO.getProfId());
		if(optSubject.isEmpty()) {
			return "Error: Subject not found";
		}
		if(optProfessor.isEmpty()) {
			return "Error: Professor not found";
		}
		Subject sub=optSubject.get();
		Professor prof=optProfessor.get();
		if(sub.getProfessors() !=null && sub.getProfessors().getProfId().equals(profDTO.getProfId())) {
			return "Error: Professor is already assigned to this Subject";
		}
		sub.setProfessor(prof);
		subRepo.save(sub);
		return "Professor Assigned successfully";
	}*/
	@Transactional
	public String assignProfessorToSubject(AssignProfessorDTO aprofDTO) {
		Optional<Professor>profOptional=profRepo.findByProfId(aprofDTO.getProfId());
		Optional<Subject>subOptional=subRepo.findById(aprofDTO.getSubId());
		if(profOptional.isPresent() && subOptional.isPresent()) {
			Professor prof=profOptional.get();
			Subject sub=subOptional.get();
			if(!sub.getProfessors().contains(prof)) {
				sub.getProfessors().add(prof);
				subRepo.save(sub);
				return"Professor assigned successfully";
			}
			return"Error: Professor is already assigned";
		}
		return"Error: Invalid Professor or Subject Id";		
	}
	public List<Subject> getSubjectsForCurrentSemester(String deptId) {
	    int currentMonth = LocalDate.now().getMonthValue(); // Get current month
	    List<Semester> currentSemesters = semRepo.findCurrentSemesters(currentMonth); 

	    if (!currentSemesters.isEmpty()) {
	        // Get the first semester (or process all if needed)
	        Semester currentSemester = currentSemesters.get(0);
	        int semNo = currentSemester.getSemNo().intValue(); 
	        return subRepo.findSubjectsBySemesterAndDepartment(semNo, deptId);
	    }
	    return Collections.emptyList(); // Return empty list if no semester found
	}
	public List<AttendSubjectDTO> findActiveSubjectsBySemesterNo(Long semNo,String deptId) {
	    List<Subject> subjects = subRepo.findBySemester_SemNoAndDepartment_DeptIdAndIsActive(semNo, deptId,true);
	    return subjects.stream()
	                   .map(subject -> new AttendSubjectDTO(subject.getSubId(), subject.getName(), subject.getSemester().getSemNo()))
	                   .collect(Collectors.toList());
	}
	public List<AttendSubjectDTO> getSubjectsForProfessor(String professorId, Long semId, String deptId) {
	    List<Subject> subjects = subRepo.findSubjectsByProfessorAndSemesterAndDepartment(professorId, semId, deptId);
	    return subjects.stream()
	            .map(subject -> new AttendSubjectDTO(subject.getSubId(), subject.getName(), subject.getSemester().getSemNo()))
	            .collect(Collectors.toList());
	}

	


}
