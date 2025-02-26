package com.example.demo.model;



import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.enums.ApprovalStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Professor {
	
	@Id
	private String profId;
	
	@Column(nullable=false,unique=true )
	private String email;
	
	@Enumerated(EnumType.STRING)
	private ApprovalStatus approvalStatus=ApprovalStatus.PENDING;
	
	@ManyToOne
	@JoinColumn(name="dept_id",nullable=false)
	@JsonBackReference
	private Department department;
	
	@OneToMany(mappedBy="mentor")
	private List<Student>students;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false)
	private String role;
	
	@Column(nullable=true)
	private String pwd;
	
	@Column(nullable=true)
	private String img;
	
	@Column(nullable=true)
	private Long phone;
	
	@Column(nullable=true)
	private Long age;

	@PrePersist
	public void prePersist() {
		this.createAt=LocalDateTime.now();
	}
	private LocalDateTime createAt;

	public String getProfId() {
		return profId;
	}

	public void setProfId(String profId) {
		this.profId = profId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	public Long getAge() {
		return age;
	}

	public void setAge(Long age) {
		this.age = age;
	}
	
	
	public LocalDateTime getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}

	public Professor() {	    
	   this.approvalStatus = ApprovalStatus.PENDING;
	}
	
	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}
	
}
