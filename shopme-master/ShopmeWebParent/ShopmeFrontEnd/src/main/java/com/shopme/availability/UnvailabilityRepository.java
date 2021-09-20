package com.shopme.availability;

import java.util.Date;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Unavailability;

public interface UnvailabilityRepository extends CrudRepository<Unavailability, Integer> {
	@Query("SELECT u FROM Unavailability u WHERE u.startDate = ?1 AND u.endDate = ?2")
	public Unavailability findByDates(Date startDate, Date endDate);
	
}
