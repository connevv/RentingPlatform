package com.shopme.shoppingcart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
		
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product.get());
		
		if (cartItem != null) {			
			throw new ShoppingCartException("Could not add more item(s)"
						+ " because there's already 1 item"
						+ "in your shopping cart. Maximum allowed quantity is 1.");
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
	
	/*public float updateQuantity(Integer productId, Integer quantity, Integer daysQuantity, Customer customer) {
		cartRepo.updateQuantity(quantity, customer.getId(), productId);
		cartRepo.updateDaysQuantity(daysQuantity, customer.getId(), productId);
		
		Product product = productRepo.findById(productId).get();
		
		float subtotal = product.getDiscountPrice() * daysQuantity * quantity;
		
		return subtotal;
	}*/
	
	public void removeProduct(Integer productId, Customer customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	public void deleteByCustomer(Customer customer) {
		cartRepo.deleteByCustomer(customer.getId());
	}
}
