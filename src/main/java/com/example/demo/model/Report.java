package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

@Entity
public class Report {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	@JoinColumn(name = "regNo", referencedColumnName = "regNo", nullable = false)
	private Student student;
	 
	@ManyToOne
    @JoinColumn(name = "subId", referencedColumnName = "subId", nullable = false)
    private Subject subject;
	
	@ManyToOne
	@JoinColumn(name = "semNo", referencedColumnName = "semNo", nullable = false)
	private Semester semester;
	
	 private int totalPeriods;      // ✅ Total periods conducted for this subject
    private int presentPeriods;    // ✅ Periods attended by the student
    private int odPeriods;         // ✅ On-Duty periods
    private int latePeriods;       // ✅ Late periods
    private int absentPeriods;
	
	public Report() {}

	  public Report(Student student, Subject subject, Semester semester, int totalPeriods, 
              int presentPeriods, int odPeriods, int latePeriods, int absentPeriods) {
		    this.student = student;
		    this.subject = subject;
		    this.semester = semester;
		    this.totalPeriods = totalPeriods;
		    this.presentPeriods = presentPeriods;
		    this.odPeriods = odPeriods;
		    this.latePeriods = latePeriods;
		    this.absentPeriods = absentPeriods;
		}

	@Transient
    public double getAttendancePercentage() {
        int effectiveTotalPeriods = totalPeriods; // OD is counted separately
        return (effectiveTotalPeriods == 0) ? 0 : 
               ((double) (presentPeriods + odPeriods) / effectiveTotalPeriods) * 100;
    }


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Semester getSemester() {
		return semester;
	}

	public void setSemester(Semester semester) {
		this.semester = semester;
	}

	public int getTotalPeriods() {
		return totalPeriods;
	}

	public void setTotalPeriods(int totalPeriods) {
		this.totalPeriods = totalPeriods;
	}

	public int getPresentPeriods() {
		return presentPeriods;
	}

	public void setPresentPeriods(int presentPeriods) {
		this.presentPeriods = presentPeriods;
	}

	public int getOdPeriods() {
		return odPeriods;
	}

	public void setOdPeriods(int odPeriods) {
		this.odPeriods = odPeriods;
	}

	public int getLatePeriods() {
		return latePeriods;
	}

	public void setLatePeriods(int latePeriods) {
		this.latePeriods = latePeriods;
	}

	public int getAbsentPeriods() {
		return absentPeriods;
	}

	public void setAbsentPeriods(int absentPeriods) {
		this.absentPeriods = absentPeriods;
	}

	

}
