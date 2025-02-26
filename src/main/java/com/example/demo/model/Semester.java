package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Semester {
	@Id
	private Long semNo;
	private Long startMonth;
	private Long endMonth;
	private LocalDateTime createAt;
	@PrePersist
	public void prePersit() {
		this.createAt=LocalDateTime.now();
	}
	public Long getSemNo() {
		return semNo;
	}
	public void setSemNo(Long semNo) {
		this.semNo = semNo;
	}
	public Long getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(Long startMonth) {
		this.startMonth = startMonth;
	}
	public Long getEndMonth() {
		return endMonth;
	}
	public void setEndMonth(Long endMonth) {
		this.endMonth = endMonth;
	}
	public LocalDateTime getCreateAt() {
		return createAt;
	}
	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}
	
}
