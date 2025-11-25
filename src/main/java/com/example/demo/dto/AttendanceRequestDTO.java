package com.example.demo.dto;

import java.time.LocalDate;

public class AttendanceRequestDTO {
	private String regNo;
	private String subId;
	private LocalDate attDate;
	private Long period;
	private String status;
	private Long semNo;
	private String markedBy; 
	private String markedByUser;
	private String batch;
	
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	public LocalDate getAttDate() {
		return attDate;
	}
	public void setAttDate(LocalDate attDate) {
		this.attDate = attDate;
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getSemNo() {
		return semNo;
	}
	public void setSemNo(Long semNo) {
		this.semNo = semNo;
	}
	public String getMarkedBy() {
		return markedBy;
	}
	public void setMarkedBy(String markedBy) {
		this.markedBy = markedBy;
	}
	public String getMarkedByUser() {
		return markedByUser;
	}
	public void setMarkedByUser(String markedByUser) {
		this.markedByUser = markedByUser;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	
	

}
