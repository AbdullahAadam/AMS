package com.example.demo.dto;

public class StudentResponseDTO {
	private String regNo;
	private String name;
	private String email;
	private String deptName;
	private String deptId;
	private String profId;
	private String profName;
	public StudentResponseDTO(String regNo, String name, String email, String deptName, String deptId,String profId,
			String profName) {
		super();
		this.regNo = regNo;
		this.name = name;
		this.email = email;
		this.deptName = deptName;
		this.deptId = deptId;
		this.profId = profId;
		this.profName = profName;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
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
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getProfId() {
		return profId;
	}
	public void setProfId(String profId) {
		this.profId = profId;
	}
	public String getProfName() {
		return profName;
	}
	public void setProfName(String profName) {
		this.profName = profName;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	
	

}
