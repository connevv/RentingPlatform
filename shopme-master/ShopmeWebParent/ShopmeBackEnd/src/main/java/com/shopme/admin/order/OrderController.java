 package com.shopme.admin.order;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.order.Order;
import com.shopme.common.exception.OrderNotFoundException;

@Controller
public class OrderController {
	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";
	
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listOrders", moduleURL = "/orders") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum,
			HttpServletRequest request,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) {

		orderService.listByPage(pageNum, helper);
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}
		
		return "orders/orders";
	}	
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Order order = orderService.get(id);		
			
			boolean isVisibleForAdminOrSalesperson = false;
			
			if (loggedUser.hasRole("Admin") || loggedUser.hasRole("RentPerson")) {
				isVisibleForAdminOrSalesperson = true;
			}
			
			model.addAttribute("isVisibleForAdminOrRentPerson", isVisibleForAdminOrSalesperson);
			model.addAttribute("order", order);
			
			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			orderService.delete(id);;
			ra.addFlashAttribute("message", "Поръчка номер " + id + " е изтрита успешно.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);;
			
			model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
			model.addAttribute("order", order);
			
			return "orders/order_form";
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}	
	
	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) throws OrderNotFoundException {
		try {
			Order oldOrder = orderService.get(order.getId());
			order.setCustomer(oldOrder.getCustomer());
			order.setProduct(oldOrder.getProduct());
			order.setStartDay(oldOrder.getStartDay());
			order.setEndDay(oldOrder.getEndDay());
			order.setOrderTime(oldOrder.getOrderTime());
			order.setRentedDays(oldOrder.getRentedDays());
			
			orderService.save(order);
		} catch (OrderNotFoundException e) {
			// TODO Auto-generated catch block
			throw new OrderNotFoundException("Поръчката не може да бъде намерена");
		}

			
		
		ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");
		
		return defaultRedirectURL;
	}
	
	/*private void sendOrderStatusChangedEmail(HttpServletRequest request, Order order) 
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
	}*/
	
}
