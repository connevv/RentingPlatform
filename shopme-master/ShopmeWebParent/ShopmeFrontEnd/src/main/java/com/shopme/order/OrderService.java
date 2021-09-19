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

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 5;
	
	@Autowired private OrderRepository repo;
	
	public Order createOrder(Customer customer, CartItem cartItem,
			PaymentMethod paymentMethod) {
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());
		
		/*if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			newOrder.setStatus(OrderStatus.PAID);
		} else {
			newOrder.setStatus(OrderStatus.NEW);
		}*/
		
		newOrder.setCustomer(customer);
		newOrder.setTotal(cartItem.getSubtotal());
		newOrder.setStartDay(cartItem.getStartDate());
		newOrder.setEndDay(cartItem.getEndDate());
		newOrder.setStatus(OrderStatus.PENDING);
		Product product = cartItem.getProduct();
		newOrder.setProduct(product);
		
		return repo.save(newOrder);
	}
	
	public Page<Order> listForCustomerByPage(Customer customer, int pageNum, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		/*if (keyword != null) {
			return repo.findAll(keyword, customer.getId(), pageable);
		}*/
		
		return repo.findAll(customer.getId(), pageable);
	}
	
	public Order getOrder(Integer id, Customer customer) {
		return repo.findByIdAndCustomer(id, customer);
	}
}
