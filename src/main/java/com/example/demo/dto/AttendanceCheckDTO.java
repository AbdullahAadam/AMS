package com.example.demo.dto;

import java.time.LocalDate;

public class AttendanceCheckDTO {
	
	private String batch;
    private LocalDate attDate;
    private Long period;
    private Long semNo;
    //private String markedByUser;

    public AttendanceCheckDTO() {}
    
    public AttendanceCheckDTO(String batch, LocalDate attDate, Long period, Long semNo/*,String subCode*/) {
		super();
		this.batch = batch;
		this.attDate = attDate;
		this.period = period;
		this.semNo = semNo;
		//this.subCode=subCode;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
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

	public Long getSemNo() {
		return semNo;
	}

	public void setSemNo(Long semNo) {
		this.semNo = semNo;
	}

	/*public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}*/
	
	

}
