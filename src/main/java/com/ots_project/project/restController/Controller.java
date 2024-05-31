package com.ots_project.project.restController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ots_project.project.entity.Customer;
import com.ots_project.project.entity.Order;
import com.ots_project.project.repositories.CustomerRepo;
import com.ots_project.project.repositories.OrderRepo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
// @Controller + @RequestBody
public class Controller {

	@Autowired
	CustomerRepo customerRepo;

	@Autowired
	OrderRepo orderRepo;

	// 1
	@Transactional
	@Operation(summary = "Add customer details", description = "Provide Customer details to add")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status 200!!  Customer added successfully."),
			@ApiResponse(responseCode = "400", description = "Status 400!! Bad Request."),
			@ApiResponse(responseCode = "500", description = "Internal Server Error.") })
	@PostMapping("/add/customer")
	public void addCustomer(@Valid @RequestBody Customer newCustomer) {
		try {
			customerRepo.save(newCustomer);
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// 2
	@Transactional
	@Operation(summary = "Update customer details by name", description = "Update Customer email and mobile number for the given customer name")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Status OK!! Updated successfully."),
			@ApiResponse(responseCode = "404", description = "Customer Not found."),
			@ApiResponse(responseCode = "400", description = "BadRequest."),
			@ApiResponse(responseCode = "500", description = "Internal Server Error.") })
	@PutMapping("/update/customer/{CustomerName}")
	public void updateCustomerDetailsByName(@PathVariable("CustomerName") String name,
			 @RequestParam("email") String customer_email, @RequestParam("Number") String customer_number) {
		Customer customer = customerRepo.findByCustName(name);
		try {
			if (customer == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found");
			}
			customer.setEmail(customer_email);
			customer.setMobile(customer_number);
			customerRepo.save(customer);
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// 3
	@Transactional
	@Operation(summary = "Delete customer details by name", description = "Delete customer details for the given name and email")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Status OK!! Updated successfully."),
			@ApiResponse(responseCode = "404", description = "Customer Not found."),
			@ApiResponse(responseCode = "400", description = "BadRequest."),
			@ApiResponse(responseCode = "500", description = "Internal Server Error.") })
	@DeleteMapping("/delete/customer/{name}")
	public void deleteCustomer(@PathVariable("name") String name, @RequestParam("email") String email) {
		Customer customer = customerRepo.findByCustNameAndEmail(name, email);
		try {
			if (customer == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "customer not found");
			}
			customerRepo.delete(customer);

		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// 4
	@Transactional
	@Operation(summary = "Update status of an order", description = "Update status and delivery date by given orderId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status 200!!Order status updated successfully."),
			@ApiResponse(responseCode = "400", description = "Bad request."),
			@ApiResponse(responseCode = "500", description = "Internal server error"),
			@ApiResponse(responseCode = "404", description = "Not found.") })
	@PutMapping("/update/order/status/{OrderId}")
	public void updateStatusOfAnOrder(@PathVariable("OrderId") String orderId, @RequestParam("Status") char newstatus,
			@RequestParam("DeliveryDate") LocalDate deliveryDate) {
		try {
			Optional<Order> optionalOrder = orderRepo.findByorderId(orderId);
			if (optionalOrder.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "orders not found");
			}
			Order order = optionalOrder.get();
			order.setStatus(newstatus);
			order.setDeliveryDate(deliveryDate);
			orderRepo.save(order);
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// 5
	@Operation(summary = "Get all customers details", description = "Retrieve all Customer details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status OK!! Retrieved all Customer details successfully."),
			@ApiResponse(responseCode = "404", description = "Not found."),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/getAllCustomers")
	public List<Customer> getAllCustomers() {
		try {
			List<Customer> customers = customerRepo.findAll();
			if (customers.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "customers not found");
			}
			return customers;
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// 6
	@Operation(summary = "Get all orders", description = "Retrieve all Orders")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status OK!! Retrieved all Orders successfully."),
			@ApiResponse(responseCode = "404", description = "Not found."),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/getAllOrders")
	public List<Order> getAllOrders() {
		try {
			List<Order> orders = orderRepo.findAll();
			if (orders.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "orders not found");
			}
			return orders;
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// 7
	@Operation(summary = "Orders ordered by a customer", description = " Get Orders Ordered By a Customer (custId)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status OK!! Retrieved order details ordered by a customer details successfully."),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/orders/ordered/{CustomerId}")
	public List<Order> ordersByCustId(@PathVariable("CustomerId") int id) {
		return orderRepo.findByCustId(id);
	}

	// 8
	@Operation(summary = "Get orders by status", description = "List of Orders with given Status")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status OK!! Retrieved Order details successfully with given status ."),
			@ApiResponse(responseCode = "404", description = "Orders Not found."),
			@ApiResponse(responseCode = "400", description = "bad request."),
			@ApiResponse(responseCode = "500", description = "Internal server error") })
	@GetMapping("/orders/by/status/{Status}")
	public List<Order> getOrdersByStatus(@PathVariable("Status") char status) {
		try {
			List<Order> orders = orderRepo.findByStatus(status);
			if (orders.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "orders not found");
			}
			return orders;
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	// 9
	@Operation(summary = "Get orders ordered between the dates", description = "Retrieves orders between given 2 dates")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Status OK!! Retrieved orders successfully."),
			@ApiResponse(responseCode = "500", description = "Internal Server Error."),
			@ApiResponse(responseCode = "400", description = "bad request."),
			@ApiResponse(responseCode = "404", description = "Orders Not found.") })
	@GetMapping("/orders/between/dates")
	public List<Order> ordersBetweenDates(@RequestParam("StartDate") LocalDate startDate,
			@RequestParam("EndDate") LocalDate endDate) {
		try {
			List<Order> orders = orderRepo.findByOrderDateBetween(startDate, endDate);
			if (orders.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Orders Not found");
			}
			return orders;
		} catch (DataAccessException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
