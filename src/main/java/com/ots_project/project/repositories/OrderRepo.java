package com.ots_project.project.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ots_project.project.entity.Order;

public interface OrderRepo extends JpaRepository<Order, String> {

	// 8
	List<com.ots_project.project.entity.Order> findByStatus(char status);

	// 14
	List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);

	// 7
	List<Order> findByCustId(int id);

	// 4
	Optional<Order> findByorderId(String orderId);

}
