package com.example.demo.dto;

import java.util.List;

public class DepartmentResponseDTO {
	private String deptId;
	private String name;
	private String hod;
	private String hodName;
	private Long period;
	private Long year;
	private String code;
	private List<DeptResponseProfDTO>professors;
	
	
	
	public DepartmentResponseDTO(String deptId, String name, String hod, String hodName, Long period, Long year,
			String code, List<DeptResponseProfDTO> professors) {
		super();
		this.deptId = deptId;
		this.name = name;
		this.hod = hod;
		this.hodName = hodName;
		this.period = period;
		this.year = year;
		this.code = code;
		this.professors = professors;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHod() {
		return hod;
	}
	public void setHod(String hod) {
		this.hod = hod;
	}
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	public Long getYear() {
		return year;
	}
	public void setYear(Long year) {
		this.year = year;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List<DeptResponseProfDTO> getProfessors() {
		return professors;
	}
	public void setProfessors(List<DeptResponseProfDTO> professors) {
		this.professors = professors;
	}
	public String getHodName() {
		return hodName;
	}
	public void setHodName(String hodName) {
		this.hodName = hodName;
	}
	
	
	
	
}
