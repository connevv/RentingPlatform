package com.shopme.common.entity.order;

public enum OrderStatus {
	
	PENDING {
		@Override
		public String defaultDescription() {
			return "Поръчката е направена от клиента и чака одобрение от наемателя";
		}
		
	}, 
	
	CANCELLED {
		@Override
		public String defaultDescription() {
			return "Поръчката е отхвърлена";
		}
	}, 
	
	APPROVED {
		@Override
		public String defaultDescription() {
			return "Поръчката е одобрена от наемателя";
		}
	};

	public abstract String defaultDescription();
}
