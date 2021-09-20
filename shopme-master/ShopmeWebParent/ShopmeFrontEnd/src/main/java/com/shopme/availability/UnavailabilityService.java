package com.shopme.availability;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Unavailability;

@Service
public class UnavailabilityService {
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private UnvailabilityRepository repo;
	
	public List<Unavailability> listAll() {
		return (List<Unavailability>) repo.findAll();
	}
	
	public Unavailability addUnavailability(Unavailability unavailability) {
		return repo.save(unavailability);
	}
	
	public Unavailability findByDates (Date startDate, Date endDate) {
		return repo.findByDates(startDate, endDate);
	}
}
