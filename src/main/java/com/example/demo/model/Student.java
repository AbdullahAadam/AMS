package com.example.demo.model;
import java.time.LocalDateTime;
import java.util.Date;
import com.example.demo.enums.LogStatus;
import com.example.demo.enums.StudentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class Student {

	@Id
	@Column(length=15,nullable=false)
	private String regNo;
	
	@ManyToOne
	@JoinColumn(name="deptId",nullable=false)
	private Department department;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable=false,unique=true)
	private String email;
	
	@Column(nullable=true)
	private String pwd;
	
	@Column(nullable=true)
	private String img;
	
	@Column(nullable=true)
	private int age;
	
	@Column(nullable=true)
	private Date dob;
	
	@Column(nullable=true,length=10)
	private String phone;
	
	@Column(nullable=true)
	private String gender;
	
	@Column(nullable=true)
	private String address;
	
	@ManyToOne
	@JoinColumn(nullable=false)
	private Professor mentor;
	
	@Column(nullable=true)
	@Temporal(TemporalType.DATE)
	private Date joinDate;
	
	@Column(nullable=true,length=5)
	private String batch;
	
	@Column(nullable=true,length=10)
	private String currentYear;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=true,columnDefinition="VARCHAR(20) DEFAULT 'PENDING'")
	private LogStatus logStatus=LogStatus.PENDING;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=true)
	private StudentStatus studentStatus;
	
	@Column(nullable=true)
	private String approvedBy;
	
	@PrePersist
	public void prePersist() {
		this.createdAt=LocalDateTime.now();
	}
	private LocalDateTime createdAt;

	
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public Department getDepartment() {
		return department;
	}
	public void setDepartment(Department department) {
		this.department = department;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Professor getMentor() {
		return mentor;
	}
	public void setMentor(Professor mentor) {
		this.mentor = mentor;
	}
	public Date getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getCurrentYear() {
		return currentYear;
	}
	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}
	public LogStatus getLogStatus() {
		return logStatus;
	}
	public void setLogStatus(LogStatus logStatus) {
		this.logStatus = logStatus;
	}
	public StudentStatus getStudentStatus() {
		return studentStatus;
	}
	public void setStudentStatus(StudentStatus studentStatus) {
		this.studentStatus = studentStatus;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreateAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
