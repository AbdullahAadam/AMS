package com.example.demo.dto;

public class DepartmentUpdateDTO {
	private String name;
	private String hod;
	private Long period;
	private Long year;
	public DepartmentUpdateDTO(String name, String hod, Long period, Long year) {
		super();
		this.name = name;
		this.hod = hod;
		this.period = period;
		this.year = year;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHod() {
		return hod;
	}
	public void setHod(String hod) {
		this.hod = hod;
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	public Long getYear() {
		return year;
	}
	public void setYear(Long year) {
		this.year = year;
	}
	

}
