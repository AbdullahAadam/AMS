package com.example.demo.dto;

public class DeptResponseProfDTO {
	private String profId;
	private String name;
	
	public DeptResponseProfDTO(String profId, String name) {
		super();
		this.profId = profId;
		this.name = name;
	}

	public String getProfId() {
		return profId;
	}

	public void setProfId(String profId) {
		this.profId = profId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	

}
