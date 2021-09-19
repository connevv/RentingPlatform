package com.shopme.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cities")
public class City extends IdBasedEntity {
	@Column(nullable = false, length = 45)
	private String name;
	
	public City() {
		
	}
	
	public City(String name) {
		super();
		this.name = name;
	}
	
	public City(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "City [name=" + name + "]";
	}
	
	
}
