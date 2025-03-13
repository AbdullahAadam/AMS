package com.example.demo.dto;

import java.util.List;

public class SubjectUpdateDTO {
	private String subId;
	private String name;
	//private String deptName;
	private String type;
	private Long semNo;
	private List<String>professorIds;
	
	public SubjectUpdateDTO(String subId, String name, /*String deptName,*/ String type, Long semNo,
			List<String> professorIds) {
		super();
		this.subId = subId;
		this.name = name;
		//this.deptName = deptName;
		this.type = type;
		this.semNo = semNo;
		this.professorIds = professorIds;
	}
	
	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	/*public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}*/
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
	
	public Long getSemNo() {
		return semNo;
	}
	public void setSemNo(Long semNo) {
		this.semNo = semNo;
	}
	public List<String> getProfessorIds() {
		return professorIds;
	}
	public void setProfessorIds(List<String> professorIds) {
		this.professorIds = professorIds;
	}
	

}
