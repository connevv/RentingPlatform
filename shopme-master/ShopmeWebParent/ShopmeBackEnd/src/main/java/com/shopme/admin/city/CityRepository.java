package com.shopme.admin.city;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.City;

public interface CityRepository extends PagingAndSortingRepository<City, Integer> {
	public City findByName(String name);
	
	@Query("SELECT NEW City(c.id, c.name) FROM City c ORDER BY c.name ASC")
	public List<City> findAll();
}
