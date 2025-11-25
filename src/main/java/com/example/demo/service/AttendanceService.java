package com.example.demo.service;


import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.demo.dto.AttendanceRequestDTO;

import com.example.demo.dto.AttendanceValidationRequest;
import com.example.demo.dto.AttendanceValidationResponse;
import com.example.demo.dto.DayAttendanceDTO;
import com.example.demo.dto.MonthAttendanceDTO;
import com.example.demo.dto.SemesterAttendanceDTO;
import com.example.demo.enums.AttendanceStatus;
import com.example.demo.model.Attendance;
import com.example.demo.model.AttendanceId;
import com.example.demo.model.Report;
import com.example.demo.model.Semester;
import com.example.demo.model.Student;
import com.example.demo.model.Subject;
import com.example.demo.repo.AttendanceRepository;
import com.example.demo.repo.ReportRepository;
import com.example.demo.repo.SemesterRepository;
import com.example.demo.repo.StudentRepository;
import com.example.demo.repo.SubjectRepository;

import jakarta.transaction.Transactional;




@Service
public class AttendanceService {
	
	@Autowired
	private AttendanceRepository attendanceRepo;
	
	@Autowired
	private SemesterRepository semRepo;
	
	@Autowired
	private SubjectRepository subjectRepo;
	
	@Autowired
	private StudentRepository studRepo;
	
	@Autowired
	private ReportRepository reportRepo;
	
	@Transactional
	public AttendanceValidationResponse validateAttendanceSlot(AttendanceValidationRequest request) {
	    // 1. Check if any attendance exists for this exact slot (date+period+batch+semester)
	    List<Attendance> existingAttendances = attendanceRepo.findBySlot(
	        request.getAttDate(),
	        request.getPeriod(),
	        request.getBatch(),
	        request.getSemNo());

	    if (existingAttendances.isEmpty()) {
	        return new AttendanceValidationResponse(false, true, null, null);
	    }

	    // 2. Check if any existing attendance is for a DIFFERENT subject
	    Optional<Attendance> conflictingAttendance = existingAttendances.stream()
	        .filter(a -> !a.getId().getSubCode().equals(request.getSubCode()))
	        .findFirst();

	    if (conflictingAttendance.isPresent()) {
	        String subjectName = subjectRepo.findById(conflictingAttendance.get().getId().getSubCode())
	            .map(Subject::getName)
	            .orElse("Unknown Subject");
	        return new AttendanceValidationResponse(true, false, subjectName, null);
	    }

	    // 3. If we get here, it's the same subject - check edit permissions
	    Attendance existing = existingAttendances.get(0);
	    return new AttendanceValidationResponse(
	        true,
	        true,
	        null,
	        existing.getMarkedBy()
	    );
	}
	
	
	
	public boolean isAttendanceExists(Long semNo, String batch, LocalDate attDate, Long period, String profId, String role) {
        if ("HOD".equalsIgnoreCase(role)) {
            // HOD can access any attendance inside their department
            return attendanceRepo.existsForHOD(semNo, batch, attDate, period, profId);
        } else {
            // Professor can access only what they have marked
            return attendanceRepo.existsForProfessor(semNo, batch, attDate, period, profId);
        }
    }
	public int getTodayMarkedSessions(String profId) {
	    return attendanceRepo.countDistinctSessionsMarkedToday(profId, LocalDate.now());
	}

	
	@Transactional
	public void markAttendance(AttendanceRequestDTO request) {
		AttendanceValidationResponse validation = validateAttendanceSlot(
	            new AttendanceValidationRequest(
	                request.getAttDate(),
	                request.getPeriod(),
	                request.getBatch(),
	                request.getSemNo(),
	                request.getSubId()));

	        if (validation.isSlotTaken() && !validation.isCanProceed()) {
	            throw new IllegalStateException(
	                "Attendance slot already taken by subject: " + validation.getExistingSubjectName());
	        }

	        // Check if existing attendance is locked
	        if (validation.isSlotTaken() && validation.getOriginalMarker() != null) {
	            List<Attendance> existing = attendanceRepo.findBySlotAndSubject(
	                request.getAttDate(),
	                request.getPeriod(),
	                request.getBatch(),
	                request.getSemNo(),
	                request.getSubId());

	            if (!existing.isEmpty() && existing.get(0).isLocked() && 
	                !"hod".equalsIgnoreCase(request.getMarkedBy())) {
	                throw new IllegalStateException("Attendance is locked and cannot be modified");
	            }
	        }
		AttendanceId attendanceId = new AttendanceId(
	            request.getRegNo(),
	            request.getSubId(),
	            request.getAttDate(),
	            request.getPeriod()
	        );
		Semester semester=semRepo.findById(request.getSemNo()) .orElseThrow(() -> new RuntimeException("Semester not found"));
		
		 Attendance attendance = attendanceRepo.findById(attendanceId)
	                .orElse(new Attendance(attendanceId, AttendanceStatus.valueOf(request.getStatus()), semester,request.getMarkedBy(),request.getMarkedByUser(),request.getBatch(),false));
		 if (attendance.isLocked() && "Professor".equalsIgnoreCase(request.getMarkedBy())) {
		        throw new RuntimeException("Attendance locked! Professor cannot update.");
		    }
		 attendance.setStatus(AttendanceStatus.valueOf(request.getStatus()));
		 attendance.setMarkedBy(request.getMarkedBy());
		 attendance.setMarkedByUser(request.getMarkedByUser());
		 attendance.setBatch(request.getBatch());
		 if ("HOD".equalsIgnoreCase(request.getMarkedBy())) {
		    attendance.setLocked(true);
		 }
		 attendanceRepo.save(attendance);
	}
	
	public void updateReport(String regNo, String subCode, Long semNo) {
	    // Fetch all attendance records for this student, subject, and semester
	    List<Attendance> attendanceRecords = attendanceRepo.findById_RegNoAndId_SubCodeAndSemester_SemNo(regNo, subCode, semNo);

	    // Track periods instead of just days
	    int totalPeriods = attendanceRecords.size();
	    int presentPeriods = 0;
	    int absentPeriods = 0;
	    int odPeriods = 0;
	    int latePeriods = 0;

	    for (Attendance attendance : attendanceRecords) {
	        switch (attendance.getStatus()) {
	            case PRESENT:
	                presentPeriods++;
	                break;
	            case ABSENT:
	                absentPeriods++;
	                break;
	            case OD:
	                odPeriods++;
	                break;
	            case LATE:
	                latePeriods++;
	                break;
	        }
	    }

	    // Fetch student, subject, and semester
	    Student student = studRepo.findById(regNo)
	            .orElseThrow(() -> new RuntimeException("Student not found!"));

	    Subject subject = subjectRepo.findById(subCode)
	            .orElseThrow(() -> new RuntimeException("Subject not found!"));

	    Semester semester = semRepo.findById(semNo)
	            .orElseThrow(() -> new RuntimeException("Semester not found!"));

	    // Check if report already exists
	    Report report = reportRepo.findByStudent_RegNoAndSubject_SubIdAndSemester_SemNo(regNo, subCode, semNo);

	    if (report == null) {
	        // Create a new report entry
	        report = new Report(
	            student, subject, semester,
	            totalPeriods, presentPeriods, absentPeriods, odPeriods, latePeriods
	        );
	    } else {
	        // Update the existing report
	        report.setTotalPeriods(totalPeriods);
	        report.setPresentPeriods(presentPeriods);
	        report.setAbsentPeriods(absentPeriods);
	        report.setOdPeriods(odPeriods);
	        report.setLatePeriods(latePeriods);
	    }

	    // Save the updated report
	    reportRepo.save(report);
	}
	public SemesterAttendanceDTO getAttendanceForSemester(String regNo, Long semesterNo) {
        Student student = studRepo.findByRegNo(regNo)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Attendance> attendanceList = attendanceRepo
                .findByIdRegNoAndSemesterSemNo(regNo, semesterNo);

        // Grouping by Month and Date
        Map<String, Map<LocalDate, List<Attendance>>> grouped = attendanceList.stream()
            .collect(Collectors.groupingBy(
                a -> a.getId().getAttDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                Collectors.groupingBy(a -> a.getId().getAttDate())
            ));

        List<MonthAttendanceDTO> monthDTOs = new ArrayList<>();

        for (Map.Entry<String, Map<LocalDate, List<Attendance>>> monthEntry : grouped.entrySet()) {
            String month = monthEntry.getKey();
            List<DayAttendanceDTO> dayDTOs = new ArrayList<>();

            for (Map.Entry<LocalDate, List<Attendance>> dayEntry : monthEntry.getValue().entrySet()) {
                LocalDate date = dayEntry.getKey();
                List<String> periodStatuses = new ArrayList<>(Collections.nCopies(6, "UNMARKED"));

                for (Attendance a : dayEntry.getValue()) {
                    int periodIndex = a.getId().getPeriod().intValue() - 1;
                    if (periodIndex >= 0 && periodIndex < 6) {
                        periodStatuses.set(periodIndex, a.getStatus().name());
                    }
                }

                DayAttendanceDTO dayDto = new DayAttendanceDTO();
                dayDto.setDate(date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date.getDayOfMonth());
                dayDto.setPeriods(periodStatuses);

                dayDTOs.add(dayDto);
            }

            MonthAttendanceDTO monthDto = new MonthAttendanceDTO();
            monthDto.setMonth(month);
            monthDto.setDays(dayDTOs);

            monthDTOs.add(monthDto);
        }

        SemesterAttendanceDTO result = new SemesterAttendanceDTO();
        result.setStudentName(student.getName());
        result.setDepartment(student.getDepartment().getDeptName());
        result.setSemesterName("Semester " + semesterNo);
        result.setMonths(monthDTOs);

        return result;
    }
	
	
	 

}
