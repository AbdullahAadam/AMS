package com.example.demo.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Holiday;
@Repository
public interface HolidayRepository extends JpaRepository<Holiday,LocalDate > {
	Optional<Holiday>findByHolidayDate(LocalDate holidayDate);
	
	@Query("SELECT h FROM Holiday h WHERE h.holidayDate >= :date ORDER BY h.holidayDate ASC")
    List<Holiday> findUpcomingHolidays(@Param("date") LocalDate date);
}
