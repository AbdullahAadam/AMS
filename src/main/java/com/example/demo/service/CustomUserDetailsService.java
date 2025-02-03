package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.Admin;
import com.example.demo.model.Professor;
import com.example.demo.model.Student;
//import com.example.demo.model.Admin;
//import com.example.demo.model.Professor;
//import com.example.demo.model.Student;
import com.example.demo.repo.AdminRepository;
import com.example.demo.repo.ProfessorRepository;
import com.example.demo.repo.StudentRepository;

 
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;

    public CustomUserDetailsService(AdminRepository adminRepository, 
                                    ProfessorRepository professorRepository, 
                                    StudentRepository studentRepository) {
        this.adminRepository = adminRepository;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	System.out.println("customuserservice "+username);
    	String[] parts=username.split(":");
   	 	if(!username.contains(":")) {
   	 		throw new UsernameNotFoundException("Invalid Format");
   		 
   	 	}
   	 	System.out.println("!!!!!!SCustomUserDetailsSerivce");
   	 	String userType=parts[0];
   	 	String email=parts[1];
   	 	System.out.println(userType);
   	 	System.out.println(email);
   	 	if("ADMIN".equals(userType)) {
   	 		Optional<Admin> adminOpt = adminRepository.findByEmail(email);
   	 		if (adminOpt.isPresent()) {
   	 			Admin admin = adminOpt.get();
   	 			List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
   	 			return new User(admin.getEmail(), admin.getPwd(), authorities);
   	 		}
   	 	}else if("PROFESSOR".equals(userType)) {
   	 		Optional<Professor> profOpt = professorRepository.findByEmail(email);
   	 		if (profOpt.isPresent()) {
   	 			Professor professor = profOpt.get();
   	 			List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
   	 			return new User(professor.getEmail(), professor.getPwd(), authorities);
   	 		}
   	 	}else if("STUDENT".equals(userType)) {
   	 		Optional<Student> studentOpt = studentRepository.findByEmail(email);
   	 		if (studentOpt.isPresent()) {
   	 			Student student = studentOpt.get();
   	 			List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
   	 			return new User(student.getEmail(), student.getPwd(), authorities);
   	 		}
   	 	}else {
   	 		
   	 	}
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}

/*@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private ProfessorRepository professorRepo;
    @Autowired
    private StudentRepository studentRepo;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	System.out.print("username cus  "+username); 
    	String[] parts=username.split(":");
    	 if(!username.contains(":")) {
    		 throw new UsernameNotFoundException("Invalid Format");
    		 
    	 }
    	 System.out.println("!!!!!!SCustomUserDetailsSerivce");
    	 String userType=parts[0];
    	 String email=parts[1];
    	 if("ROLE_ADMIN".equals(userType) ) {
    		 return adminRepo.findByEmail(email).map(admin-> new User(admin.getEmail(),admin.getPwd(),List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))))
    				 .orElseThrow(()->new UsernameNotFoundException("Admin not found")); 	
    	 }else if ("professor".equals(userType)) {
             return professorRepo.findByEmail(email)
                     .map(professor -> new User(professor.getEmail(), professor.getPwd(),
                             List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"))))
                     .orElseThrow(() -> new UsernameNotFoundException("Professor not found"));

             } else if ("student".equals(userType)) {
                 return studentRepo.findByEmail(email)
                     .map(student -> new User(student.getEmail(), student.getPwd(),
                             List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))))
                     .orElseThrow(() -> new UsernameNotFoundException("Student not found"));
             }

             throw new UsernameNotFoundException("User type not recognized");
    }
 }*/
/*@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final ProfessorRepository professorRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(AdminRepository adminRepository, ProfessorRepository professorRepository, 
                                    StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.professorRepository = professorRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔍 Searching for user with email: " + email);

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            System.out.println("✅ Admin found: " + email);
            return User.withUsername(admin.get().getEmail())
                    .password(admin.get().getPwd())
                    .roles("ADMIN")
                    .build();
        }

        Optional<Professor> professor = professorRepository.findByEmail(email);
        if (professor.isPresent()) {
            System.out.println("✅ Professor found: " + email);
            return User.withUsername(professor.get().getEmail())
                    .password(professor.get().getPwd())
                    .roles("PROFESSOR")
                    .build();
        }

        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            System.out.println("✅ Student found: " + email);
            return User.withUsername(student.get().getEmail())
                    .password(student.get().getPwd())
                    .roles("STUDENT")
                    .build();
        }

        System.out.println("❌ User not found in any table!");
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}*/
    	
//    	if (adminRepo.findByEmail(email).isPresent()) {
//             Admin admin = adminRepo.findByEmail(email).get();
//             return new User(admin.getEmail(), admin.getPwd(), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
//         } else if (professorRepo.findByEmail(email).isPresent()) {
//             Professor professor = professorRepo.findByEmail(email).get();
//             return new User(professor.getEmail(), professor.getPwd(), List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR")));
//         } else if (studentRepo.findByEmail(email).isPresent()) {
//             Student student = studentRepo.findByEmail(email).get();
//             return new User(student.getEmail(), student.getPwd(), List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
//         }
//         else {
//            throw new UsernameNotFoundException("Email not found"+email);
//        }
    

