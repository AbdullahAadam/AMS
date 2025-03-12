package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Holiday;
import com.example.demo.repo.HolidayRepository;

@Service
public class HolidayService {
	@Autowired
	private HolidayRepository holidayRepo;
	public String addHoliday(Holiday holiday) {
		 System.out.println("HolidayService received: " + holiday);
		Optional<Holiday>existingHoliday=holidayRepo.findByHolidayDate(holiday.getHolidayDate());
		if(existingHoliday.isPresent()) {
			return"Error: The Holiday date already exists. ";
		}
		holidayRepo.save(holiday);
		return"Holiday added successfuly";
	
	}
}
