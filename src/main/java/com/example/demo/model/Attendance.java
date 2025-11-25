package com.example.demo.model;

import com.example.demo.enums.AttendanceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Attendance {
	
	@EmbeddedId
	private AttendanceId id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AttendanceStatus status;
	
	@ManyToOne
    @JoinColumn(name = "semNo", referencedColumnName = "semNo", nullable = false)
    private Semester semester;
	
	@Column(nullable = false)
    private String markedBy; 

    @Column(nullable = false)
    private String markedByUser; 
    
    @Column(nullable = false)
    private String batch; 

    @Column(nullable = false)
    private boolean locked = false;
	
	public Attendance() {}

	

	public Attendance(AttendanceId id, AttendanceStatus status, Semester semester, String markedBy, String markedByUser,String batch,
			boolean locked) {
		super();
		this.id = id;
		this.status = status;
		this.semester = semester;
		this.markedBy = markedBy;
		this.markedByUser = markedByUser;
		this.batch=batch;
		this.locked = locked;
	}



	public AttendanceId getId() {
		return id;
	}

	public void setId(AttendanceId id) {
		this.id = id;
	}

	public AttendanceStatus getStatus() {
		return status;
	}

	public void setStatus(AttendanceStatus status) {
		this.status = status;
	}

	public Semester getSemester() {
		return semester;
	}

	public void setSemester(Semester semester) {
		this.semester = semester;
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



	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	
	

}
