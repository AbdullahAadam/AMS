package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Department {
	@Id
	private String deptId;
	
	@OneToMany(mappedBy="department",cascade=CascadeType.ALL,orphanRemoval=true)
	@JsonManagedReference
	private List<Professor>professors;
	
	@OneToMany(mappedBy="department")
	private List<Student>students;
	
	@Column(nullable=false)
	private String deptName;
	
	@Column(nullable=false ,unique=true)
	private String code;
	
	@Column(nullable=false)
	private Long year;
	
	@Column(nullable=false)
	private Long period;
	
	@Column(nullable=false)
	private Long sem;
	
	private LocalDateTime createdAt;
	
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getYear() {
		return year;
	}
	public void setYear(Long year) {
		this.year = year;
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	public Long getSem() {
		return sem;
	}
	public void setSem(Long sem) {
		this.sem = sem;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	@PrePersist
	public void prePersist() {
		this.createdAt=LocalDateTime.now();
	}
	public List<Professor> getProfessors() {
		return professors;
	}
	public void setProfessors(List<Professor> professors) {
		this.professors = professors;
	}
	public List<Student> getStudents() {
		return students;
	}
	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
	
}
