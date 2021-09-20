package com.shopme.order;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.OrderNotFoundException;

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 5;
	
	@Autowired private OrderRepository repo;
	
	public Order createOrder(Customer customer, CartItem cartItem) {
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());
		newOrder.setCustomer(customer);
		newOrder.setTotal(cartItem.getSubtotal());
		newOrder.setStartDay(cartItem.getStartDate());
		newOrder.setEndDay(cartItem.getEndDate());
		newOrder.setStatus(OrderStatus.PENDING);
		newOrder.setRentedDays((int)cartItem.getRentedDays());
		Product product = cartItem.getProduct();
		newOrder.setProduct(product);
		
		return repo.save(newOrder);
	}
	
	public Page<Order> listForCustomerByPage(Customer customer, int pageNum, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, customer.getId(), pageable);
		}
		
		return repo.findAll(customer.getId(), pageable);
	}
	
	public Order getOrder(Integer id, Customer customer) {
		return repo.findByIdAndCustomer(id, customer);
	}
	
	public void delete(Integer id) throws OrderNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Поръчка с номер" + id + "не може да бъде намерена"); 
		}
		
		repo.deleteById(id);
	}
}
