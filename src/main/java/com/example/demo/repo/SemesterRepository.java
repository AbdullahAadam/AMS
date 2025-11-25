package com.example.demo.repo;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//import com.example.demo.model.Department;
import com.example.demo.model.Semester;

@Repository
public interface SemesterRepository extends JpaRepository<Semester,Long>{
	Optional<Semester>findBySemNo(Long semNo);
	
	 Semester findTopByOrderBySemNoDesc();
	 
	
	@Query("SELECT s FROM Semester s WHERE :currentMonth BETWEEN s.startMonth AND s.endMonth ORDER BY s.semNo ASC")
	Semester findCurrentSemester(@Param("currentMonth") int currentMonth);
	
	@Query("SELECT s FROM Semester s WHERE :currentMonth BETWEEN s.startMonth AND s.endMonth ORDER BY s.semNo ASC")
	List<Semester> findCurrentSemesters(@Param("currentMonth") int currentMonth);
	
	//@Query("SELECT s FROM Semester s WHERE s.semNo = :semNo AND s.department = :department")
	//Optional<Semester> findBySemNoAndDepartment(@Param("semNo") Long semNo, @Param("department") Department department);
	
	 List<Semester> findBySemNoLessThanEqual(Long semNo);

	
	
	 
	 


}
