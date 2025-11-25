package com.example.demo.dto;

public class AttendSubjectDTO {
	private String subId;
	private String name;
	private Long semNo;
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
	public AttendSubjectDTO(String subId, String name, Long semNo) {
		super();
		this.subId = subId;
		this.name = name;
		this.semNo = semNo;
	}
	

}
