package com.shopme.common.entity.order;

public enum OrderStatus {
	
	PENDING {
		@Override
		public String defaultDescription() {
			return "Order was placed by the customer and is waiting for approval from the renter";
		}
		
	}, 
	
	CANCELLED {
		@Override
		public String defaultDescription() {
			return "Order was rejected";
		}
	}, 
	
	APPROVED {
		@Override
		public String defaultDescription() {
			return "Order is approved by the renter";
		}
	};

	public abstract String defaultDescription();
}
