package com.shopme.common.entity.order;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.IdBasedEntity;
import com.shopme.common.entity.product.Product;

@Entity
@Table(name = "orders")
public class Order extends IdBasedEntity {
	private Date orderTime;
	private float total;
	
	@Column(name = "rented_days")
	private int rentedDays;
	
	@Column(name = "rent_start_day")
	private Date startDay;
	
	@Column(name = "rent_end_day")
	private Date endDay;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 45, nullable = false)
	private OrderStatus status;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public int getRentedDays() {
		return rentedDays;
	}

	public void setRentedDays(int rentedDays) {
		this.rentedDays = rentedDays;
	}

	public Date getStartDay() {
		return startDay;
	}

	public void setStartDay(Date startDay) {
		this.startDay = startDay;
	}

	public Date getEndDay() {
		return endDay;
	}

	public void setEndDay(Date endDay) {
		this.endDay = endDay;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", status=" + status
				+ ", customer=" + customer.getFullName() + "]";
	}
	
	@Transient
	public String getProductCity() {
		String destination =  this.product.getCity() + ", ";
		destination += "Bulgaria";
		
		return destination;
	}
	

	
	/*@Transient
	public String getDeliverDateOnForm() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormatter.format(this.deliverDate);
	}	*/
	
	/*public void setDeliverDateOnForm(String dateString) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
 		
		try {
			this.deliverDate = dateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 		
	}*/
	
	@Transient
	public String getRecipientName() {
		String name = this.customer.getFirstName();
		String lastName = this.customer.getLastName();
		if (lastName != null && !lastName.isEmpty()) name += " " + lastName;
		return name;
	}
	
	@Transient
	public String getRenterName() {
		String name = this.product.getUser().getFirstName();
		String lastName = this.product.getUser().getLastName();
		if (lastName != null && !lastName.isEmpty()) name += " " + lastName;
		return name;
	}	
	
	@Transient
	public boolean isPending() {
		return hasStatus(OrderStatus.PENDING);
	}
	
	@Transient
	public boolean isApproved() {
		return hasStatus(OrderStatus.APPROVED);
	}
	
	@Transient
	public boolean isCanceled() {
		return hasStatus(OrderStatus.CANCELLED);
	}
	
	public boolean hasStatus(OrderStatus status) {
			if (getStatus().equals(status)) {
				return true;
			}
		
		return false;
	}
	
	@Transient
	public String getProductNames() {
		return "<ul><li>" + product.getShortName() + "</li></ul>";
	}
}
