package com.example.demo.dto;

import java.util.List;

public class MonthAttendanceDTO {
	 private String month;
	 private List<DayAttendanceDTO> days;
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public List<DayAttendanceDTO> getDays() {
		return days;
	}
	public void setDays(List<DayAttendanceDTO> days) {
		this.days = days;
	}
	 
	 

}
