package com.example.demo.dto;

public class ProfessorDTO {
	private String profId;
	private String name;
	private String email;
	private String department;
	public ProfessorDTO(String profId, String name, String email, String department) {
		super();
		this.profId = profId;
		this.name = name;
		this.email = email;
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
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	

}
