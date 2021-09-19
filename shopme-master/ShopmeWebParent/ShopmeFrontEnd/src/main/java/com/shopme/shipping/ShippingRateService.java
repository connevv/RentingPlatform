package com.shopme.shipping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;

@Service
public class ShippingRateService {

	@Autowired private ShippingRateRepository repo;
	
	public ShippingRate getShippingRateForCustomer(Customer customer) {
		
		return repo.findByCity(customer.getCity());
	}
	
	public ShippingRate getShippingRateForAddress(Address address) {
		
		return repo.findByCity(address.getCity());
	}
}
