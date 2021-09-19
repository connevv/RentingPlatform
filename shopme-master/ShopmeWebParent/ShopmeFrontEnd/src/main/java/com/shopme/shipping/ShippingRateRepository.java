package com.shopme.shipping;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
	
	public ShippingRate findByCity(String city);
}
