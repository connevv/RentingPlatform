package com.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.shopme.common.entity.product.Product;

@Entity
@Table(name = "comments")
public class Comment extends IdBasedEntity{
	@Column(nullable = false, length = 45)
	private String name;
	
	private Date date;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	public Comment() {
		
	}
	
	public Comment(Integer id,String name, Product product, Customer customer, Date date) {
		super();
		this.name = name;
		this.product = product;
		this.customer = customer;
		this.date = date;
	}
	
	public Comment(String name, Product product, Customer customer, Date date) {
		super();
		this.name = name;
		this.product = product;
		this.customer = customer;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
