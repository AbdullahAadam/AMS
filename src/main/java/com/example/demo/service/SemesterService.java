package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Semester;
import com.example.demo.model.Student;
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
	public Long getCurrentSemester() {
	    LocalDate currentDate = LocalDate.now();
	    Semester semester = semRepo.findCurrentSemester(currentDate.getMonthValue());
	    
	    if (semester != null) {
	        return semester.getSemNo(); // No need to convert
	    } else {
	        throw new RuntimeException("Current semester not found");
	    }
	}
	
	public List<Semester>getSemestersForStudent(Student stud){
		Long courseDuration=stud.getDepartment().getYear();
		Long totalSemester=courseDuration *2;
		return semRepo.findBySemNoLessThanEqual(totalSemester);
	}


}
