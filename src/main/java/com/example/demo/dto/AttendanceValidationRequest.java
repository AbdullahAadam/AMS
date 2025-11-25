package com.example.demo.dto;

import java.time.LocalDate;

public class AttendanceValidationRequest  {
	
	 private LocalDate attDate;
	    private Long period;
	    private String batch;
	    private Long semNo;
	    private String subCode;
	    
	    
		public AttendanceValidationRequest(LocalDate attDate, Long period, String batch, Long semNo, String subCode) {
			super();
			this.attDate = attDate;
			this.period = period;
			this.batch = batch;
			this.semNo = semNo;
			this.subCode = subCode;
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
		public String getBatch() {
			return batch;
		}
		public void setBatch(String batch) {
			this.batch = batch;
		}
		public Long getSemNo() {
			return semNo;
		}
		public void setSemNo(Long semNo) {
			this.semNo = semNo;
		}
		public String getSubCode() {
			return subCode;
		}
		public void setSubCode(String subCode) {
			this.subCode = subCode;
		}
	
}
