package com.example.demo.dto;

public class AttendanceValidationResponse {
	
	private boolean slotTaken;
    private boolean canProceed;
    private String existingSubjectName;
    private String originalMarker;
    
    public AttendanceValidationResponse() {}
	public AttendanceValidationResponse(boolean slotTaken, boolean canProceed, String existingSubjectName,
			String originalMarker) {
		super();
		this.slotTaken = slotTaken;
		this.canProceed = canProceed;
		this.existingSubjectName = existingSubjectName;
		this.originalMarker = originalMarker;
	}
	public boolean isSlotTaken() {
		return slotTaken;
	}
	public void setSlotTaken(boolean slotTaken) {
		this.slotTaken = slotTaken;
	}
	public boolean isCanProceed() {
		return canProceed;
	}
	public void setCanProceed(boolean canProceed) {
		this.canProceed = canProceed;
	}
	public String getExistingSubjectName() {
		return existingSubjectName;
	}
	public void setExistingSubjectName(String existingSubjectName) {
		this.existingSubjectName = existingSubjectName;
	}
	public String getOriginalMarker() {
		return originalMarker;
	}
	public void setOriginalMarker(String originalMarker) {
		this.originalMarker = originalMarker;
	}
	 
	 

}
