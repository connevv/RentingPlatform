package com.shopme.admin.city;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.brand.BrandRepository;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.City;

@Service
public class CityService {
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private CityRepository repo;
	
	public List<City> listAll() {
		return (List<City>) repo.findAll();
	}
}
