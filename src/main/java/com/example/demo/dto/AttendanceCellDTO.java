package com.example.demo.dto;

public class AttendanceCellDTO {
	
	private String status;
    private String subjectName;
    private String subjectCode;
    private String markedBy;

    // Constructors
    public AttendanceCellDTO() {}

    public AttendanceCellDTO(String status, String subjectName, String subjectCode, String markedBy) {
        this.status = status;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.markedBy = markedBy;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getMarkedBy() {
        return markedBy;
    }

    public void setMarkedBy(String markedBy) {
        this.markedBy = markedBy;
    }

}
