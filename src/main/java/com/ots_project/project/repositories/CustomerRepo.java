package com.ots_project.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ots_project.project.entity.Customer;

//jpaRepo or CrudRepo 
public interface CustomerRepo extends JpaRepository<Customer, Integer> {

	// 1 b
	Customer findByCustName(String name);

	// 1 c
	Customer findByCustNameAndEmail(String name, String email);
}
