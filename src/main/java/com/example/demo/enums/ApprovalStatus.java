package com.example.demo.enums;

public enum ApprovalStatus {
	PENDING("Pending"),
	ACCEPTED("Accepted"),
	REJECTED("Rejected");
	
	private final String description;
	ApprovalStatus(String description){
		this.description=description;
	}
	public String getDescription() {
		return description;
	}
	@Override
    public String toString() {
        return description;
    }
}
