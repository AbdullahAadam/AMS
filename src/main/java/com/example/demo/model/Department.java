package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Department {
	@Id
	private String deptId;
	
	@OneToMany(mappedBy="department",fetch=FetchType.EAGER)
	//@JsonManagedReference
	@JsonIgnore
	private List<Professor>professors;
	
	@OneToMany(mappedBy="department")
	//@JsonManagedReference
	@JsonIgnore
	private List<Student>students;
	
	@OneToMany(mappedBy="department")
	@JsonIgnore
	private List<Subject>subjects;
	
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
	
	@Column(nullable = false)
	private boolean isActive=true;
	
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
	public List<Subject> getSubjects() {
		return subjects;
	}
	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public Professor getHod() {
	    if (professors != null) {
	        for (Professor professor : professors) {
	            if ("HOD".equalsIgnoreCase(professor.getRole())) {
	                return professor;
	            }
	        }
	    }
	    return null;
	}

	
	
}
