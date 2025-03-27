package com.example.demo.dto;

import com.example.demo.model.Subject;

public class AssignSubDTO {
	private String subId;
    private String name;
    private Long semNo;
    public AssignSubDTO(Subject subject) {
        this.subId = subject.getSubId();
        this.name = subject.getName();
        this.semNo = subject.getSemester() != null ? subject.getSemester().getSemNo() : 0;
    }
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
	public Long getSemNo() {
		return semNo;
	}
	public void setSemNo(Long semNo) {
		this.semNo = semNo;
	}
    

}
