package com.example.demo.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long>{
	
	Report findByStudent_RegNoAndSubject_SubIdAndSemester_SemNo(String regNo, String subId, Long semNo);
	
	List<Report>findByStudent_RegNoAndSemester_SemNo(String regNo,Long semNo);

}
