package com.example.demo.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Department;
@Repository
public interface DepartmentRepository extends JpaRepository<Department,String>{
	Optional<Department>findByCode(String code);
	Long countByIsActiveTrue();
	@Query("Select d from Department d WHERE d.isActive=true") 
	List<Department>findActiveDepartments();
	  
	@Query("SELECT COUNT(d) > 0 FROM Department d JOIN d.professors p WHERE p.profId = :hodId AND d.deptId <> :deptId AND p.role = 'HOD'")
	boolean isHodAlreadyAssigned(String hodId, String deptId);
	
	 @Query("SELECT d.period FROM Department d WHERE d.deptId = :deptId")
	 Long getTotalPeriodByDepartmentId(@Param("deptId") String deptId);
	
	
	//boolean existsByHod_ProfIdAndDeptIdNot(String profId,String deptId);

}
