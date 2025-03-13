package com.example.demo.dto;

public class StudentUpdateDTO {
	private String name;
	private String email;
	private String profId;
	public StudentUpdateDTO(String name, String email, String profId) {
		super();
		this.name = name;
		this.email = email;
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
	public String getProfId() {
		return profId;
	}
	public void setProfId(String profId) {
		this.profId = profId;
	}
	

}
