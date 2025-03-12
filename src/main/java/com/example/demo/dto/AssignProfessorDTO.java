package com.example.demo.dto;

public class AssignProfessorDTO {
	private String profId;
	private String subId;
	public AssignProfessorDTO(String profId, String subId) {
		super();
		this.profId = profId;
		this.subId = subId;
	}
	public String getProfId() {
		return profId;
	}
	public void setProfId(String profId) {
		this.profId = profId;
	}
	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	
	

}
