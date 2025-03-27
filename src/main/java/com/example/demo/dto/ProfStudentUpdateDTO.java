package com.example.demo.dto;

import java.util.Date;

import com.example.demo.enums.StudentStatus;

public class ProfStudentUpdateDTO {
	private String regNo;
	private String name;
	private String batch;
	private Date joinDate;
	private StudentStatus status;
	private String year;
	public ProfStudentUpdateDTO(String regNo, String name, String batch, Date joinDate, StudentStatus status,String year) {
		super();
		this.regNo = regNo;
		this.name = name;
		this.batch = batch;
		this.joinDate = joinDate;
		this.status = status;
		this.year=year;
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
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
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
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	

}
