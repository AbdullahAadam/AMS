package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Semester;
import com.example.demo.repo.SemesterRepository;

@Service
public class SemesterService {
	@Autowired
	private SemesterRepository semRepo;
	public String addSemester(Semester sem) {
		Optional<Semester>existingSemNo=semRepo.findBySemNo(sem.getSemNo());
		if(existingSemNo.isPresent()) {
			return"Error: Semester No already exists";
		}
		semRepo.save(sem);
		return"Semester No added successfully";
	}
}
