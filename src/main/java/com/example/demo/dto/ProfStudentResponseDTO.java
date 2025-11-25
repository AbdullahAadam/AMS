package com.example.demo.dto;

import java.util.Date;

import com.example.demo.enums.StudentStatus;

public class ProfStudentResponseDTO {
	private String regNo;
	private String dept;
	private String name;
	private String batch;
	private String year;
	private String gender;
	private Date joinDate;
	private StudentStatus status;
	public ProfStudentResponseDTO(String regNo, String dept, String name, String batch, String year,String gender, Date joinDate,
			StudentStatus status) {
		super();
		this.regNo = regNo;
		this.dept = dept;
		this.name = name;
		this.batch = batch;
		this.year = year;
		this.gender = gender;
		this.joinDate = joinDate;
		this.status = status;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public StudentStatus getStatus() {
		return status;
	}
	public void setStatus(StudentStatus status) {
		this.status = status;
	}
	
	
	

}
