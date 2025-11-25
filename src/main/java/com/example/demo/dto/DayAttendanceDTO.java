package com.example.demo.dto;

import java.util.List;

public class DayAttendanceDTO {
	 private String date;
	 private List<String> periods;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public List<String> getPeriods() {
		return periods;
	}
	public void setPeriods(List<String> periods) {
		this.periods = periods;
	}
	 
	 

}
