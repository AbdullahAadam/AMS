package com.example.demo.dto;

public class AttendanceResponseDTO {
	private String regNo;
    private String status;
    private String markedBy; // "PROFESSOR" or "HOD"
    private String markedByUser;
    private String batch;
    private boolean locked;
    public AttendanceResponseDTO(String regNo, String status, String markedBy, String markedByUser,String batch,boolean locked) {
        this.regNo = regNo;
        this.status = status;
        this.markedBy = markedBy;
        this.markedByUser = markedByUser;
        this.batch=batch;
        this.locked = locked;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getStatus() {
        return status;
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

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}
    

}
