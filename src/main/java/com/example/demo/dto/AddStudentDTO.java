package com.example.demo.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.enums.LogStatus;
import com.example.demo.enums.StudentStatus;

public class AddStudentDTO {
	private String regNo;
	private String name;
	private String email;
	private String deptId;
	private String profId;
	private String password;
	private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private LogStatus logStatus;
	private StudentStatus studStatus;
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
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getProfId() {
		return profId;
	}
	public void setProfId(String profId) {
		this.profId = profId;
	}
	public LogStatus getLogStatus() {
		return logStatus;
	}
	public void setLogStatus(LogStatus logStatus) {
		this.logStatus = logStatus;
	}
	public StudentStatus getStudStatus() {
		return studStatus;
	}
	public void setStudStatus(StudentStatus studStatus) {
		this.studStatus = studStatus;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = passwordEncoder.encode(password);
	}
	
	
	

}
