package com.example.demo.dto;

public class SubjectAddDTO {
	private String subId;
	private String name;
	private String type;
	private String deptId;
	private Long semNo;
	public SubjectAddDTO(String subId, String name, String type, String deptId, Long semNo) {
		super();
		this.subId = subId;
		this.name = name;
		this.type = type;
		this.deptId = deptId;
		this.semNo = semNo;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public Long getSemNo() {
		return semNo;
	}
	public void setSemNo(Long semNo) {
		this.semNo = semNo;
	}
	
	

}
