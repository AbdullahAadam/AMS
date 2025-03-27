package com.example.demo.dto;

import com.example.demo.model.Professor;

public class AssingProfDTO {
	private String profId;
    private String name;
    public AssingProfDTO(Professor professor) {
        this.profId = professor.getProfId();
        this.name = professor.getName();
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
