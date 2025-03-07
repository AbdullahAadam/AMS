package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
//import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
//import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "subId")
@Entity
public class Subject {
	@Id
	private String subId;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private String type;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	@JsonIgnore
	private Department department;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	//@JsonIgnore
	@JsonManagedReference
	private Semester semester;
	
	@ManyToMany
	@JoinTable(
	    name = "subject_professor",
	    joinColumns = @JoinColumn(name = "sub_id"),
	    inverseJoinColumns = @JoinColumn(name = "prof_id")
	)
	@JsonManagedReference //one way only sub->prof
	private List<Professor> professors;
	
	
	@PrePersist
	public void prePersist() {
		this.createdAt=LocalDateTime.now();
	}
	private LocalDateTime createdAt;


	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public Semester getSemester() {
		return semester;
	}
	public void setSemester(Semester semester) {
		this.semester = semester;
	}
	
	public List<Professor> getProfessors() {
		return professors;
	}
	public void setProfessors(List<Professor> professors) {
		this.professors = professors;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	

}
