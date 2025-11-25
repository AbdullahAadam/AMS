package com.example.demo.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class AttendanceId  implements Serializable{
	
	
	private static final long serialVersionUID = 1L;	
	private String regNo; // Student Registration Number
    private String subCode; // Subject Code
    private LocalDate attDate; // Attendance Date
    private Long period; 
    
    public AttendanceId() {}

	public AttendanceId(String regNo, String subCode, LocalDate attDate, Long period) {
		super();
		this.regNo = regNo;
		this.subCode = subCode;
		this.attDate = attDate;
		this.period = period;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
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
    
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    AttendanceId that = (AttendanceId) o;
	    return Objects.equals(regNo, that.regNo) &&
	           Objects.equals(subCode, that.subCode) &&
	           Objects.equals(attDate, that.attDate) &&
	           Objects.equals(period, that.period); // Fixed comparison
	}


    @Override
    public int hashCode() {
        return Objects.hash(regNo, subCode, attDate, period);
    }

}
