package com.example.demo.repo;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,String>{
	List<Subject>findByDepartment_DeptId(String deptId);
	//boolean existsByProfessor_ProfIdAndSubId(String profId,String subId);
	@Query("Select s from Subject s WHERE s.isActive=true")
	List<Subject>findActiveSubjects();
	Long countByIsActiveTrue();
	
	@Query("SELECT s FROM Subject s WHERE s.department.deptId = :deptId " +
	           "AND s.semester.semNo BETWEEN :startSem AND :endSem")
	    List<Subject> findSubjectsByDepartmentAndYear(@Param("deptId") String deptId,
	                                                  @Param("startSem") int startSem,
	                                                  @Param("endSem") int endSem);
	
	@Query("SELECT s FROM Subject s WHERE s.semester.semNo = :semNo AND s.department.deptId = :deptId")
    List<Subject> findSubjectsBySemesterAndDepartment(@Param("semNo") int semNo, @Param("deptId") String deptId);
	
	List<Subject>findBySemester_SemNoAndDepartment_DeptIdAndIsActive(Long semNo,String deptId,boolean isActive);

	@Query("SELECT s FROM Subject s JOIN s.professors p WHERE p.id = :professorId AND s.semester.semNo = :semId AND s.department.id = :deptId")
	List<Subject> findSubjectsByProfessorAndSemesterAndDepartment(
	    @Param("professorId") String professorId,
	    @Param("semId") Long semId,
	    @Param("deptId") String deptId
	);



}
