package com.shopme.shoppingcart;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.availability.UnavailabilityService;
import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.Unavailability;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.product.Product;
import com.shopme.common.entity.setting.EmailSettingBag;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.product.ProductService;
import com.shopme.setting.PaymentSettingBag;
import com.shopme.setting.SettingService;
import com.shopme.shipping.ShippingRateService;

@Controller
public class ShoppingCartController {
	@Autowired private CustomerService customerService;
	@Autowired private ShoppingCartService cartService;
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;
	@Autowired private UnavailabilityService unavailabilityService;
	@Autowired private ProductService productService;
	
	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		CartItem cartItem = cartService.listCartItem(customer);
		String startDay = null;
		String endDay = null;
		long estimatedRentedDays = 0;
		if (cartItem != null) {
			startDay = new SimpleDateFormat("EEEE").format(cartItem.getStartDate());
			endDay = new SimpleDateFormat("EEEE").format(cartItem.getEndDate());
			estimatedRentedDays = cartItem.getRentedDays();
		}
		
		model.addAttribute("cartItem", cartItem);
		model.addAttribute("esitmatedRentedDays", estimatedRentedDays);
		model.addAttribute("startDay", startDay);
		model.addAttribute("endDay", endDay);
		return "cart/shopping_cart";
	}
	
	@GetMapping("/checkout")
	public String placeOrder(Model model, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		Customer customer = getAuthenticatedCustomer(request);
		CartItem cartItem = cartService.listCartItem(customer);
		
		cartService.deleteByCustomer(customer);
		
		Order createdOrder = orderService.createOrder(customer, cartItem);
		
		Product product = createdOrder.getProduct();
		Set<Unavailability> takenDates = product.getUnavailabilities();
		Unavailability newUnavailability = new Unavailability(createdOrder.getStartDay(), createdOrder.getEndDay());
		takenDates.add(newUnavailability);
		unavailabilityService.addUnavailability(newUnavailability);
		product.setUnavailabilities(takenDates);
		
		productService.saveProduct(product);
		sendOrderConfirmationEmail(request, createdOrder);
		
		return "cart/order_completed";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
	
	private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) 
			throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		mailSender.setDefaultEncoding("utf-8");
		
		String toAddress = order.getCustomer().getEmail();
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
		
		subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		DateFormat dateFormatter =  new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime = dateFormatter.format(order.getOrderTime());
		
		String totalAmount = Float.toString(order.getTotal()) + " BGN";
		
		content = content.replace("[[name]]", order.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(order.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[total]]", totalAmount);
		
		helper.setText(content, true);
		mailSender.send(message);		
	}
}
