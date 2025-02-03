package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor,String>{
	
	Optional<Professor> findByEmail(String email);
	//boolean existByEmail(String email);

}
