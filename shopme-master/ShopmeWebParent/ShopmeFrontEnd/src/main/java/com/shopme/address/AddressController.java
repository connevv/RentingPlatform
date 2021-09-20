package com.shopme.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.city.CityService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.City;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

@Controller
public class AddressController {
	@Autowired private CustomerService customerService;
	
	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);

		model.addAttribute("customer", customer);
		model.addAttribute("usePrimaryAddressAsDefault", true);
		
		return "address_book/addresses";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}		
	
	/*@GetMapping("/address_book/new")
	public String newAddress(Model model) {	
		List<City> listCities = cityService.listAll();
		
		model.addAttribute("address", new Address());
		model.addAttribute("pageTitle", "Add New Address");
		model.addAttribute("listCities", listCities);
		
		return "address_book/address_form";
	}
	
	@PostMapping("/address_book/save")
	public String saveAddress(Address address, HttpServletRequest request, RedirectAttributes ra) {
		Customer customer = getAuthenticatedCustomer(request);
		
		address.setCustomer(customer);
		addressService.save(address);
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}
		
		ra.addFlashAttribute("message", "The address has been saved successfully.");
		
		return redirectURL;
	}
	
	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id") Integer addressId, Model model,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		//List<Country> listCountries = customerService.listAllCountries();
		
		Address address = addressService.get(addressId, customer.getId());

		model.addAttribute("address", address);
		//model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");
		
		return "address_book/address_form";
	}
	
	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.delete(addressId, customer.getId());
		
		ra.addFlashAttribute("message", "The address ID " + addressId + " has been deleted.");
		
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId,
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		addressService.setDefaultAddress(addressId, customer.getId());
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/checkout";
		}
		
		return redirectURL; 
	}*/
}
