package com.example.demo.dto;

import com.example.demo.model.Department;

public class ProfessorResponseDTO {
	private String profId;
	private String name;
	private String email;
	private String role;
	private Department department;
	public ProfessorResponseDTO(String profId, String name, String email, String role, Department department) {
		super();
		this.profId = profId;
		this.name = name;
		this.email = email;
		this.role = role;
		this.department = department;
	}
	public String getProfId() {
		return profId;
	}
	public void setProfId(String profId) {
		this.profId = profId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	};
	

}
