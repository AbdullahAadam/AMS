package com.example.demo.dto;

import com.example.demo.model.Department;

public class AssignDeptDTO {
	 private String deptId;
	 private String deptName;
	 private String code;
	 public AssignDeptDTO(Department department) {
	        this.deptId = department.getDeptId();
	        this.deptName = department.getDeptName();
	        this.code = department.getCode();
	  }
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	 
	    
}
