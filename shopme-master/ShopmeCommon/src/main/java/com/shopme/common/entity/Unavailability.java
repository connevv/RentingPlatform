package com.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "unavailability")
public class Unavailability extends IdBasedEntity{
	@Column(name = "rent_start_day")
	private Date startDate;
	
	@Column(name = "rent_end_day")
	private Date endDate;
	
	public Unavailability() {
		
	}
	
	public Unavailability(Integer id,Date startDate, Date endDate) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
	}


	public Unavailability(Date startDay, Date endDay) {
		super();
		this.startDate = startDay;
		this.endDate = endDay;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "Unvailability [startDay=" + startDate + ", endDay=" + endDate + "]";
	}
}
