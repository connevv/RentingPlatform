package com.shopme.user;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.User;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.customer.CustomerService;

@Controller
public class UserController {
	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	@Autowired private UserService userService;
	@Autowired private CustomerService customerService;
	
	@GetMapping("/user/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, 
			Model model, HttpServletRequest request,
			RedirectAttributes redirectAttributes) {
		try {
			boolean canProductBeVoted = true;
			User userRenter = userService.getById(id);
			String message = "";
			Customer customer = getAuthenticatedCustomer(request);
			if (customer != null) {
				for(User user :customer.getUsers()) {
					if (user.getId() == user.getId()) {
						canProductBeVoted = false;
						message = message.concat("Не можеш да гласуваш понеже вече си го направил.");
					}
				}
			} else {
				canProductBeVoted = false;
				message = message.concat("Не можеш да гласуваш без да се регистрираш.");
			}
			
			
			
			model.addAttribute("user", userRenter);
			model.addAttribute("pageTitle", "Информация за потребител (Ид. номер: " + id + ")");
			model.addAttribute("canBeVoted", canProductBeVoted);
			model.addAttribute("votesMessage", message);
			
			return "user/user_form";
		} catch (UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/user/{user_id}/{vote_type}/{vote}")
	public String updateProductVotesStatus(@PathVariable("user_id") String userId,
			@PathVariable("vote_type") String voteType,
			@PathVariable("vote") String vote, HttpServletRequest request, RedirectAttributes redirectAttributes) throws ProductNotFoundException, NumberFormatException, UserNotFoundException {
		
		
		User user = userService.getById(Integer.parseInt(userId));
		
		userService.updateVote(user.getId(), voteType, Integer.parseInt(vote));
		Customer customer = getAuthenticatedCustomer(request);
		
		Set<User> listVotedUsers = customer.getUsers();
		listVotedUsers.add(user);
		customer.setUsers(listVotedUsers);
		customerService.update(customer);
		
		return "redirect:/user/" + userId;
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
}
