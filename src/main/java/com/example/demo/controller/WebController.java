package com.example.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.enums.ApprovalStatus;
import com.example.demo.model.Department;
import com.example.demo.model.Professor;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.ProfessorService;

@Controller
public class WebController {
	@Autowired
	private DepartmentService deptService;
	@Autowired
	private ProfessorService profService;
	
	@GetMapping("/admin/login")
	public String adminLoginPage() {
		System.out.println("Admin Loging");
		return "admin/login";
	}
	@GetMapping("/professor/login")
	public String professorLoginPage() {
		return "professor/login";
	}
	@GetMapping("/student/login")
	public String studentLoginPage() {
		return "student/login";
	}
	@GetMapping("/listDepartments")
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> getDepartments() {
	    List<Department> departments = deptService.getAllDepartments();
	    
	    // Create a response with only required fields
	    List<Map<String, Object>> response = new ArrayList<>();
	    for (Department dept : departments) {
	        Map<String, Object> deptData = new HashMap<>();
	        deptData.put("deptId", dept.getDeptId());
	        deptData.put("deptName", dept.getDeptName());
	        deptData.put("sem", dept.getSem());
	        deptData.put("code", dept.getCode());// Include only relevant semester info
	        response.add(deptData);
	    }
	    
	    return ResponseEntity.ok(response);
	}
	@GetMapping("/professors/{deptId}")
	@ResponseBody
	public List<Professor>getProfessorsByDepartmentId(@PathVariable String deptId){
		//List<Professor>getProfessorsByDepartmentId
		List<Professor> allProfessors= profService.getProfessorsByDepartmentId(deptId,ApprovalStatus.ACCEPTED);
		//return allProfessors.stream().filter(prof->prof.getProfId().startsWith("PROF-")).collect(Collectors.toList());
		return allProfessors.stream().filter(prof->"prof".equalsIgnoreCase(prof.getRole())).collect(Collectors.toList());
	}

	
}
