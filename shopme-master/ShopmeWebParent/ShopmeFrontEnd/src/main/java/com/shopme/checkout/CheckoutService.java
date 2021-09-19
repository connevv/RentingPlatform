package com.shopme.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@Service
public class CheckoutService {
	private static final int DIM_DIVISOR = 139;

	/*public CheckoutInfo prepareCheckout(List<CartItem> cartItems, ShippingRate shippingRate) {
		CheckoutInfo checkoutInfo = new CheckoutInfo();
		
		float productTotal = calculateProductTotal(cartItems);
		float shippingCostTotal = calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = productTotal + shippingCostTotal;
		
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setShippingCostTotal(shippingCostTotal);
		checkoutInfo.setPaymentTotal(paymentTotal);
		
		checkoutInfo.setDeliverDays(shippingRate.getDays());
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		
		return checkoutInfo;
	}*/

	/*private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float shippingCostTotal = 0.0f;
		
		for (CartItem item : cartItems) {
			Product product = item.getProduct();
			float shippingCost = 30 * item.getQuantity() * shippingRate.getRate();
			
			item.setShippingCost(shippingCost);
			
			shippingCostTotal += shippingCost;
		}
		
		return shippingCostTotal;
	}*/

	private float calculateProductTotal(List<CartItem> cartItems) {
		float total = 0.0f;
		
		for (CartItem item : cartItems) {
			total += item.getSubtotal();
		}
		
		return total;
	}
}
