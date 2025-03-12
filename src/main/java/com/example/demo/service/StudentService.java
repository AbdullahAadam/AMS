package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.StudentResponseDTO;
import com.example.demo.dto.StudentUpdateDTO;
import com.example.demo.enums.LogStatus;
import com.example.demo.model.Professor;
import com.example.demo.model.Student;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.StudentRepository;

@Service
public class StudentService {
	@Autowired
	private StudentRepository studRepo;
	@Autowired
	private ProfessorRepository profRepo;
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
	public StudentResponseDTO getStudentById(String regNo) {
		return studRepo.findById(regNo)
				.map(student->new StudentResponseDTO(
						student.getRegNo(),
						student.getName(),
						student.getEmail(),
						student.getDepartment().getDeptName(),
						student.getDepartment().getDeptId(),
						student.getMentor().getProfId(),
						student.getMentor().getName())).orElse(null);
	}
	public boolean updateStudent(String regNo,StudentUpdateDTO updateStudent) {
		Optional<Student>optionalStudent=studRepo.findById(regNo);
		if(optionalStudent.isPresent()) {
			Student stud=optionalStudent.get();
			stud.setName(updateStudent.getName());
			stud.setEmail(updateStudent.getEmail());
			Professor prof=profRepo.findByProfId(updateStudent.getProfId()).orElseThrow(() -> new RuntimeException("Professor not found"));
			stud.setMentor(prof);
			studRepo.save(stud);
			return true;
		}
		return false;
		
	}
	public void deleteStudent(String regNo) {
		Student stud=studRepo.findById(regNo).orElseThrow(() -> new RuntimeException("Student not found!"));
		studRepo.delete(stud);
	}
}
