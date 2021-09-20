package com.shopme.shoppingcart;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.product.ProductRepository;

@Service
@Transactional
public class ShoppingCartService {

	@Autowired private CartItemRepository cartRepo;
	@Autowired private ProductRepository productRepo;
	
	public Integer addProduct(Integer productId, Customer customer, Date startDate, Date endDate) 
			throws ShoppingCartException {
		Optional<Product> product = productRepo.findById(productId);
		
		CartItem cartItem = cartRepo.findByCustomer(customer);
		
		if (cartItem != null) {		
			throw new ShoppingCartException("Не може да добавиш повече продукти"
						+ " защото вече има 1 артикул във вашата кошница."
						+ " Максимално допустимото количество е 1.");
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product.get());
			cartItem.setStartDate(startDate);
			cartItem.setEndDate(endDate);
		}
		
		cartRepo.save(cartItem);
		
		return 1;
	}
	
	public CartItem listCartItem(Customer customer) {
		return cartRepo.findByCustomer(customer);
	}
	
	public void removeProduct(Integer productId, Customer customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	public void deleteByCustomer(Customer customer) {
		cartRepo.deleteByCustomer(customer.getId());
	}
}
