package com.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.entity.product.Product;

@Entity
@Table(name = "cart_items")
public class CartItem extends IdBasedEntity {
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "product_id")	
	private Product product;
	
	@Column(name = "rent_start_day")
	private Date startDate;
	
	@Column(name = "rent_end_day")
	private Date endDate;
	
	public CartItem() {
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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

	@Transient
	public long getRentedDays() {
		long diff = endDate.getTime() - startDate.getTime(); 
		long daysBetweenDates = (diff / (1000*60*60*24));
		
		if (daysBetweenDates == 0) {
			daysBetweenDates += 1;
		}
		return daysBetweenDates;
	}

	@Override
	public String toString() {
		return "CartItem [id=" + id + ", customer=" + customer.getFullName() + ", product=" + product.getShortName() + "]";
	}

	@Transient
	public float getSubtotal() {
		return product.getDiscountPrice() * getRentedDays();
	}
}
