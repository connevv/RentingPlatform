package com.shopme.admin.city;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.City;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CityRepositoryTest {

	@Autowired
	private CityRepository repo;
	
	@Test
	public void testCreateCity() {
		City testCity = new City("Burgas");
		
		City savedCity = repo.save(testCity);
		
		assertThat(savedCity).isNotNull();
		assertThat(savedCity.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testGetByCity() {	
		City savedCity = repo.findByName("Sofia");
		
		assertThat(savedCity).isNotNull();
		assertEquals(savedCity.getName(), "Sofia");
	}
}
