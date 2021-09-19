package com.shopme.admin.shippingrate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.admin.paging.SearchRepository;
import com.shopme.common.entity.ShippingRate;

public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer> {
	
	@Query("SELECT sr FROM ShippingRate sr WHERE sr.city = ?1")
	public ShippingRate findByCity(String city);
	
	@Query("UPDATE ShippingRate sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
	@Modifying
	public void updateCODSupport(Integer id, boolean enabled);
	
	@Query("SELECT sr FROM ShippingRate sr WHERE sr.city LIKE %?1%")
	public Page<ShippingRate> findAll(String keyword, Pageable pageable);
	
	public Long countById(Integer id);
}
