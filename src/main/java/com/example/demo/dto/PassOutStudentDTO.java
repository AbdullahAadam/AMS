package com.example.demo.dto;

import java.util.Date;

public class PassOutStudentDTO {
	private String name;
	private String regNo;
	private String department;
	private String phone;
	private String img;
	private Date joinDate;
	private String batch;
	public PassOutStudentDTO(String name, String regNo, String department, String phone, String img,Date joinDate, String batch) {
		super();
		this.name = name;
		this.regNo = regNo;
		this.department = department;
		this.phone = phone;
		this.img = img;
		this.joinDate=joinDate;
		this.batch = batch;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
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
	
	

}
