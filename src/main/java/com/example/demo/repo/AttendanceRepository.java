package com.example.demo.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceId;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,AttendanceId>{
	 
	List<Attendance> findById_SubCodeAndId_AttDateAndId_Period(String subId, LocalDate attDate, Long period);
	
	List<Attendance>findById_RegNoAndId_SubCodeAndSemester_SemNo(String regNo,String subCode,Long semNo);
	
	@Query("SELECT DISTINCT a.semester.semNo FROM Attendance a WHERE a.id.regNo = :regNo")
	List<Integer> findDistinctSemestersByRegNo(@Param("regNo") String regNo);
	
	@Query("SELECT DISTINCT MONTH(a.id.attDate) FROM Attendance a WHERE a.id.regNo = :regNo AND a.semester.semNo = :semester")
	List<Integer> findDistinctMonths(@Param("regNo") String regNo, @Param("semester") Long semester);

	@Query("SELECT a FROM Attendance a WHERE a.id.regNo = :regNo AND a.semester.semNo = :semester AND MONTH(a.id.attDate) = :month")
	List<Attendance> findByStudentSemesterMonth(@Param("regNo") String regNo,
	                                            @Param("semester") Long semester,
	                                            @Param("month") int month);

	
	List<Attendance>findById_RegNoAndId_AttDate(String regNo, LocalDate today);
	
	 List<Attendance> findByIdRegNoAndSemesterSemNo(String regNo, Long semNo);
	
	@Query("SELECT MIN(a.id.attDate) FROM Attendance a WHERE a.semester.semNo = :semNo")
    LocalDate findFirstAttendanceDateBySemester(@Param("semNo") Long semNo);
	
	@Query("SELECT MAX(a.id.attDate) FROM Attendance a WHERE a.semester.semNo = :semNo")
	LocalDate findLastAttendanceDateBySemester(@Param("semNo") Long semNo);

	
	@Query("SELECT COUNT(DISTINCT a.id.period) " +
		       "FROM Attendance a " +
		       "WHERE a.markedByUser = :profId AND a.id.attDate = :date")
		int countDistinctSessionsMarkedToday(@Param("profId") String profId,
		                                     @Param("date") LocalDate date);

	
	
	 @Query("SELECT COUNT(a) > 0 FROM Attendance a WHERE a.semester.semNo = :semNo AND a.batch = :batch AND a.id.attDate = :attDate AND a.id.period = :period AND " +
	           "(a.markedByUser = :profId OR EXISTS (SELECT p FROM Professor p WHERE p.profId = :profId AND p.role = 'HOD' AND p.department.id = " +
	           "(SELECT p2.department.id FROM Professor p2 WHERE p2.profId = a.markedByUser)))")
	    boolean existsForHOD(Long semNo, String batch, LocalDate attDate, Long period, String profId);
	 
	 @Query("SELECT COUNT(a) > 0 FROM Attendance a WHERE a.semester.semNo = :semNo AND a.batch = :batch AND a.id.attDate = :attDate AND a.id.period = :period AND a.markedByUser = :profId")
	 boolean existsForProfessor(@Param("semNo") Long semNo, @Param("batch") String batch, @Param("attDate") LocalDate attDate, @Param("period") Long period, @Param("profId") String profId);

	
	 @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
	           "FROM Attendance a WHERE " +
	           "a.id.attDate = :date AND " +
	           "a.id.period = :period AND " +
	           "a.batch = :batch AND " +
	           "a.semester.semNo = :semNo")
	    boolean existsBySlot(
	        @Param("date") LocalDate date,
	        @Param("period") Long period,
	        @Param("batch") String batch,
	        @Param("semNo") Long semNo);

	    // Check if attendance exists with same slot but different subject
	    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
	           "FROM Attendance a WHERE " +
	           "a.id.attDate = :date AND " +
	           "a.id.period = :period AND " +
	           "a.batch = :batch AND " +
	           "a.semester.semNo = :semNo AND " +
	           "a.id.subCode <> :subCode")
	    boolean existsBySlotWithDifferentSubject(
	        @Param("date") LocalDate date,
	        @Param("period") Long period,
	        @Param("batch") String batch,
	        @Param("semNo") Long semNo,
	        @Param("subCode") String subCode);

	    // Find attendance for a specific slot and subject
	    @Query("SELECT a FROM Attendance a WHERE " +
	           "a.id.attDate = :date AND " +
	           "a.id.period = :period AND " +
	           "a.batch = :batch AND " +
	           "a.semester.semNo = :semNo AND " +
	           "a.id.subCode = :subCode")
	    List<Attendance> findBySlotAndSubject(
	        @Param("date") LocalDate date,
	        @Param("period") Long period,
	        @Param("batch") String batch,
	        @Param("semNo") Long semNo,
	        @Param("subCode") String subCode);
	    
	    @Query("SELECT a FROM Attendance a WHERE " +
	    	       "a.id.attDate = :date AND " +
	    	       "a.id.period = :period AND " +
	    	       "a.batch = :batch AND " +
	    	       "a.semester.semNo = :semNo")
	    	List<Attendance> findBySlot(
	    	    @Param("date") LocalDate date,
	    	    @Param("period") Long period,
	    	    @Param("batch") String batch,
	    	    @Param("semNo") Long semNo);
	 
	 
	 
	 
	/*@Query("SELECT COUNT(a) > 0 FROM Attendance a " +
    "WHERE a.semester.semNo = :semNo " +
    "AND a.batch = :batch " +
    "AND a.id.attDate = :attDate " +  // Corrected reference
    "AND a.id.period = :period")     // Corrected reference
boolean existsAttendance(@Param("semNo") Long semNo, 
                      @Param("batch") String batch, 
                      @Param("attDate") LocalDate attDate, 
                      @Param("period") Long period);*/




	
}
