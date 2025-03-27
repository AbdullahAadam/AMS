package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Semester;
@Repository
public interface SemesterRepository extends JpaRepository<Semester,Long>{
	Optional<Semester>findBySemNo(Long semNo);
	
	 Semester findTopByOrderBySemNoDesc();
	
	@Query("SELECT s FROM Semester s WHERE :currentMonth BETWEEN s.startMonth AND s.endMonth ORDER BY s.semNo ASC")
	Semester findCurrentSemester(@Param("currentMonth") int currentMonth);


}
