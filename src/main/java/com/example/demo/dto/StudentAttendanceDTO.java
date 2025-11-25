package com.example.demo.dto;

public class StudentAttendanceDTO {
	private String regNo;
	private String name;
	private boolean locked; // True if HOD updated it
    private String markedBy;

	public StudentAttendanceDTO() {
    }
	public StudentAttendanceDTO(String regNo, String name) {
		super();
		this.regNo = regNo;
		this.name = name;
		
		this.locked = false;
	    this.markedBy = null;
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
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public String getMarkedBy() {
		return markedBy;
	}
	public void setMarkedBy(String markedBy) {
		this.markedBy = markedBy;
	}
	
	

}
