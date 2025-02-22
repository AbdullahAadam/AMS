package com.example.demo.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Holiday;
@Repository
public interface HolidayRepository extends JpaRepository<Holiday,LocalDate > {
	Optional<Holiday>findByHolidayDate(LocalDate holidayDate);
}
