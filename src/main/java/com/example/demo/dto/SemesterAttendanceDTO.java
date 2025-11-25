package com.example.demo.dto;

import java.util.List;

public class SemesterAttendanceDTO {
	private String studentName;
    private String department;
    private String semesterName;
    private List<MonthAttendanceDTO> months;
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getSemesterName() {
		return semesterName;
	}
	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}
	public List<MonthAttendanceDTO> getMonths() {
		return months;
	}
	public void setMonths(List<MonthAttendanceDTO> months) {
		this.months = months;
	}
    
    

}
