package com.example.demo.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.enums.ApprovalStatus;

public class ProfessorAddDTO {
	private String name;
	private String email;
	private String deptId;
	private String role;
	private ApprovalStatus approvalStatus;
	private String profId;
	private String password;
	private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	public ProfessorAddDTO(String name, String email, String deptId, String role, ApprovalStatus approvalStatus,
			String profId,String password) {
		super();
		this.name = name;
		this.email = email;
		this.deptId = deptId;
		this.role = role;
		this.approvalStatus = approvalStatus;
		this.profId = profId;
		this.password=password;
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
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
	public String getProfId() {
		return profId;
	}
	public void setProfId(String profId) {
		this.profId = profId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = passwordEncoder.encode(password);
	}
	
	
}
