package com.shopme.product;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.paging.PagingAndSortingHelper;
import com.shopme.paging.PagingAndSortingParam;
import com.shopme.category.CategoryService;
import com.shopme.comment.CommentService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Comment;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.CategoryNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.customer.CustomerService;

@Controller
public class ProductController {
	//private final String defaultRedirectCommentURL = "redirect:/comments/page/1?sortField=name&sortDir=asc";
	
	@Autowired private ProductService productService;
	@Autowired private CategoryService categoryService;
	@Autowired private CustomerService customerService;
	@Autowired private CommentService commentService;

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
			Model model) {
		return viewCategoryByPage(alias, 1, model);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable("pageNum") int pageNum,
			Model model) {
		try {
			Category category = categoryService.getCategory(alias);		
			List<Category> listCategoryParents = categoryService.getCategoryParents(category);
			
			Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
			List<Product> listProducts = pageProducts.getContent();
			
			long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			if (endCount > pageProducts.getTotalElements()) {
				endCount = pageProducts.getTotalElements();
			}
			
			
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageProducts.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProducts.getTotalElements());
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", category);
			model.addAttribute("CURRENCY_SYMBOL_POSITION", "After price");
			model.addAttribute("CURRENCY_SYMBOL", "BGN");
			
			return "product/products_by_category";
		} catch (CategoryNotFoundException ex) {
			return "error/404";
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return "error/404";
		}
	}
	
	@SuppressWarnings("deprecation")
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,  HttpServletRequest request) {
		
		try {
			Product product = productService.getProduct(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());
			
			boolean canProductBeRented = true;
			//Date takenUntil = product.getTakenUntil();
			SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
			Date takenUntil = myFormat.parse("23 07 2021");
			Date today = new Date();
			
			int result = today.compareTo(takenUntil);
			if (result < 0) {
				canProductBeRented = false;
				String dateUntilCanBeRented = takenUntil.toString() + "/" + takenUntil.getMonth() + "/" + takenUntil.getYear();
				model.addAttribute("dateUntilCanBeRented", dateUntilCanBeRented.substring(0, 10));
			}
			
			boolean canProductBeVoted = true;
			
			String message = "";
			Customer customer = getAuthenticatedCustomer(request);
			if (customer != null) {
				for(Product productItem :customer.getProducts()) {
					if (productItem.getId() == product.getId()) {
						canProductBeVoted = false;
						message = message.concat("Не можеш да гласуваш понеже вече си го направил.");
					}
				}
			} else {
				canProductBeVoted = false;
				message = message.concat("Не можеш да гласуваш без да се регистрираш.");
			}
			
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getShortName());
			model.addAttribute("canBeRented", canProductBeRented);
			model.addAttribute("canBeVoted", canProductBeVoted);
			model.addAttribute("votesMessage", message);
			model.addAttribute("comment", new Comment());

			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		} catch (ParseException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(String keyword, String city, Model model) {
		return searchByPage(keyword, city, 1, model);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(String keyword, String city,
			@PathVariable("pageNum") int pageNum,
			Model model) {
		Page<Product> pageProducts = productService.search(keyword, pageNum);
		Page<Product> pageByCityProducts = productService.searchByCity(city, pageNum);
		
		List<Product> listResult = pageProducts.getContent();
		List<Product> listByCityResult = pageByCityProducts.getContent();
		
		String message = "";
		
		if (listResult.size() == 0 && listByCityResult.size() > 0) {
			model.addAttribute("listResult", listByCityResult);
			message = message.concat("Search Results for '" + city + "'");
		} else if (listResult.size() > 0 && listByCityResult.size() == 0) {
			model.addAttribute("listResult", listResult);
			message = message.concat("Search Results for '" + keyword + "'");
		} else if (listResult.size() > 0 && listByCityResult.size() > 0) {
			List<Product> totalProductsList = new ArrayList<Product>(listResult);
			for(Product product : listByCityResult) {
				if (!listResult.contains(product)) {
					totalProductsList.add(product);
				}
			}
			message = message.concat("Search Results for '" + keyword + "' in city '" + city + "'");
			model.addAttribute("listResult", totalProductsList);
		} else {
			model.addAttribute("listResult", listResult);
			if (keyword != "" && city != "") {
				message = message.concat("No match found for '" + keyword + "' in city '" + city + "'");
			} else if (keyword != "" && city == "") {
				message = message.concat("No match found for '" + keyword + "'");
			} else if (keyword == "" && city != "") {
				message = message.concat("No match found for '" + city + "'");
			} else {
				message = message.concat("No match found. Please enter eather product or city.");
			}
			
		}
		
		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProducts.getTotalPages() + pageByCityProducts.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements() + pageByCityProducts.getTotalElements());
		model.addAttribute("pageTitle", keyword + " - Search Result");
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("city", city);
		model.addAttribute("message", message);
		
		
		return "product/search_result";
	}
	
	@GetMapping("/p/{product_alias}/{vote_type}/{vote}")
	public String updateProductVotesStatus(@PathVariable("product_alias") String alias,
			@PathVariable("vote_type") String voteType,
			@PathVariable("vote") String vote, HttpServletRequest request, RedirectAttributes redirectAttributes) throws ProductNotFoundException {
		
		Product product = productService.getProduct(alias);
		
		productService.updateVote(product.getId(), voteType, Integer.parseInt(vote));
		Customer customer = getAuthenticatedCustomer(request);
		
		Set<Product> listVotedProducts = customer.getProducts();
		listVotedProducts.add(product);
		customer.setProducts(listVotedProducts);
		customerService.update(customer);
		
		return "redirect:/p/" + alias;
	}
	
	@GetMapping("/p/{product_alias}/comments")
	public String listFirstPage(@PathVariable("product_alias") String alias, Model model) throws Exception {
		return viewProductComments(alias, 1, "date", "asc", "", model);
	}
	
	@GetMapping("/p/{product_alias}/comments/page/{pageNum}")
	public String viewProductComments(@PathVariable("product_alias") String alias,
			@PathVariable(name = "pageNum") int pageNum,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword,
			Model model) throws ProductNotFoundException {
		
		Product product = productService.getProduct(alias);
		//commentService.getCommentsByProductsId(product, null);
		
		Page<Comment> page =commentService.listByPage(pageNum,  product, sortField, sortDir, keyword);
		List<Comment> listComments = page.getContent();
		
		long startCount = (pageNum - 1) * CommentService.PRODUCTS_PER_PAGE + 1;
		long endCount = startCount + CommentService.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc"; 
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listComments", listComments);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("productAlias", product.getAlias());
		
		return "comment/comments";
	}
	
	@PostMapping("/p/{product_alias}/comment/save")
	public String saveComment(@PathVariable("product_alias") String alias, Comment comment, HttpServletRequest request, RedirectAttributes ra) {
		String redirectURL = "redirect:/p/" + alias;
		try {
			Customer customer = getAuthenticatedCustomer(request);
			comment.setCustomer(customer);
			Product product = productService.getProduct(alias);
			comment.setProduct(product);
			comment.setDate(new Date());
			
			commentService.createComment(comment);

			ra.addFlashAttribute("message", "The comment has been added successfully.");
			return redirectURL;
		} catch (ProductNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return redirectURL;
		
		
		
	}
	
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
}
