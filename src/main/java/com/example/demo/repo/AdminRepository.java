package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin,String> {
	Optional<Admin> findByEmail(String email);
	//Admin findByEmails(String email);
	//boolean existByEmail(String email);
}
