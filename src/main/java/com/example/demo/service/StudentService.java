package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.enums.LogStatus;
import com.example.demo.model.Student;
import com.example.demo.repo.StudentRepository;

@Service
public class StudentService {
	@Autowired
	private StudentRepository studRepo;
	public String addStudent(Student stud) {
		Optional<Student>existingRegNo=studRepo.findByRegNo(stud.getRegNo());
		Optional<Student>existingEmail=studRepo.findByEmail(stud.getEmail());
		if(existingRegNo.isPresent()) {
			return"Error: Student already exists. ";
		}
		if(existingEmail.isPresent()) {
			return"Error: Email already exists. ";
		}
		studRepo.save(stud);
		return "Student add Successfully";
	}
	public List<Student>getStudentByLogStatus(LogStatus status){
		return studRepo.findByLogStatus(status);
	}
	public void acceptStudent(String regNo) {
		Student stud=studRepo.findById(regNo).orElseThrow(()->new RuntimeException("Student not found"));
		stud.setLogStatus(LogStatus.APPROVED);
		studRepo.save(stud);
	}
	public void rejectedStudent(String regNo) {
		Student stud=studRepo.findById(regNo).orElseThrow(()->new RuntimeException("Student not found"));
		stud.setLogStatus(LogStatus.REJECTED);
		studRepo.deleteById(regNo);
	}

}
