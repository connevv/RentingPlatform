package com.shopme.shoppingcart;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.customer.CustomerService;

@RestController
public class ShoppingCartRestController {
	@Autowired private ShoppingCartService cartService;
	@Autowired private CustomerService customerService;
	
	@PostMapping("/cart/add/{productId}/{startDay}/{endDay}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("startDay") String startDay, @PathVariable("endDay") String endDay, HttpServletRequest request) {
		
		try {
			SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
			Customer customer = getAuthenticatedCustomer(request);
			Date test = myFormat.parse(startDay);
			Integer updatedQuantity = cartService.addProduct(productId, customer, myFormat.parse(startDay), myFormat.parse(endDay));
			
			return updatedQuantity + " item of this product were added to your shopping cart.";
		} catch (CustomerNotFoundException ex) {
			return "You must login to add this product to cart.";
		} catch (ShoppingCartException ex) {
			return ex.getMessage();	
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
		
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) 
			throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if (email == null) {
			throw new CustomerNotFoundException("No authenticated customer");
		}
				
		return customerService.getCustomerByEmail(email);
	}
	
	/*@PostMapping("/cart/update/{productId}/{quantity}/{daysQuantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, @PathVariable("daysQuantity") Integer daysQuantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subtotal = cartService.updateQuantity(productId, quantity, daysQuantity, customer);
			
			return String.valueOf(subtotal);
		} catch (CustomerNotFoundException ex) {
			return "You must login to change quantity of product.";
		}	
	}*/
	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeProduct(@PathVariable("productId") Integer productId,
			HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			cartService.removeProduct(productId, customer);
			
			return "The product has been removed from your shopping cart.";
			
		} catch (CustomerNotFoundException e) {
			return "You must login to remove product.";
		}
	}
}
