package com.shopme.admin.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.availability.UnavailabilityService;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.city.CityService;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.City;
import com.shopme.common.entity.Unavailability;
import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	private String defaultRedirectURL = "redirect:/products/page/1?sortField=name&sortDir=asc&categoryId=0";
	@Autowired private ProductService productService;
	@Autowired private BrandService brandService;
	@Autowired private CategoryService categoryService;
	@Autowired private CityService cityService;
	@Autowired private UnavailabilityService unavailabilityService;
	
	@GetMapping("/products")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/page/{pageNum}")
	public String listByPage(
			@PagingAndSortingParam(listName = "listProducts", moduleURL = "/products") PagingAndSortingHelper helper,
			@PathVariable(name = "pageNum") int pageNum, Model model,
			Integer categoryId, @AuthenticationPrincipal ShopmeUserDetails loggedUser
			) {
		
		
		if (loggedUser.hasRole("RentPerson")) {
			productService.listByPageRenter(pageNum, helper, categoryId, loggedUser.getId());
		} else {
			productService.listByPage(pageNum, helper, categoryId);
		}
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		if (categoryId != null) model.addAttribute("categoryId", categoryId);
		model.addAttribute("listCategories", listCategories);
		
		return "products/products";		
	}
	
	@GetMapping("/products/new")
	public String newProduct(Model model, @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		List<Brand> listBrands = brandService.listAll();
		List<City> listCities = cityService.listAll();
		List<Category> listCategories = new ArrayList<>();
		categoryService.listAll().forEach(Cagegory -> {
			listCategories.add(Cagegory);
		});
		
		Product product = new Product();
		product.setEnabled(true);
		
		boolean isLoggedUserAdmin = false;
		if (loggedUser.hasRole("Admin")) {
			isLoggedUserAdmin = true;
		}
		
		model.addAttribute("product", product);
		model.addAttribute("listBrands", listBrands);
		model.addAttribute("listCities", listCities);
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Product");
		model.addAttribute("numberOfExistingExtraImages", 0);
		model.addAttribute("isLoggedUserAdmin", isLoggedUserAdmin);
		
		return "products/product_form";
	}
	
	@PostMapping("/products/save")
	public String saveProduct(Product product, RedirectAttributes ra,
			@RequestParam(value = "fileImage", required = false) MultipartFile mainImageMultipart,			
			@RequestParam(value = "extraImage", required = false) MultipartFile[] extraImageMultiparts,
			@RequestParam(name = "detailIDs", required = false) String[] detailIDs,
			@RequestParam(name = "detailNames", required = false) String[] detailNames,
			@RequestParam(name = "detailValues", required = false) String[] detailValues,
			@RequestParam(name = "unavailabilityIDs", required = false) String[] unavailabilityIDs,
			@RequestParam(name = "unavailabilityStartDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date [] unavailabilityStartDate,
			@RequestParam(name = "unavailabilityEndDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date[] unavailabilityEndDate,
			@RequestParam(name = "imageIDs", required = false) String[] imageIDs,
			@RequestParam(name = "imageNames", required = false) String[] imageNames,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser
			) throws IOException {
		
		/*if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
			if (loggedUser.hasRole("Salesperson")) {
				productService.saveProductPrice(product);
				ra.addFlashAttribute("message", "The product has been saved successfully.");			
				return defaultRedirectURL;
			}
		}*/
		
		ProductSaveHelper.setMainImageName(mainImageMultipart, product);
		ProductSaveHelper.setExistingExtraImageNames(imageIDs, imageNames, product);
		ProductSaveHelper.setNewExtraImageNames(extraImageMultiparts, product);
		ProductSaveHelper.setProductDetails(detailIDs, detailNames, detailValues, product);
		
		List<Unavailability> allUnavailableTermins = unavailabilityService.listAll();
		for (int i = 0; i < unavailabilityStartDate.length; i++) {
			if (unavailabilityStartDate[i] != null && unavailabilityEndDate[i] != null) {
				Unavailability newUnavailibility = new Unavailability(unavailabilityStartDate[i], unavailabilityEndDate[i]);
				Unavailability fromDatabase = unavailabilityService.findByDates(unavailabilityStartDate[i], unavailabilityEndDate[i]);
				if (fromDatabase == null) {
					Unavailability savedUnavailability = unavailabilityService.addUnavailability(newUnavailibility);
					product.addUnavailability(savedUnavailability);
				}
			}
		}
		
		//ProductSaveHelper.setProductUnavailabilities(unavailabilityIDs, unavailabilityStartDate, unavailabilityEndDate, product);
		Product savedProduct = productService.save(product, loggedUser);
		
		ProductSaveHelper.saveUploadedImages(mainImageMultipart, extraImageMultiparts, savedProduct);
		
		ProductSaveHelper.deleteExtraImagesWeredRemovedOnForm(product);
		
		ra.addFlashAttribute("message", "The product has been saved successfully.");
		
		return defaultRedirectURL;
	}

	
	@GetMapping("/products/{id}/enabled/{status}")
	public String updateProductEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		productService.updateProductEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The Product ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/delete/{id}")
	public String deleteProduct(@PathVariable(name = "id") Integer id, 
			Model model, RedirectAttributes redirectAttributes) {
		try {
			productService.delete(id);
			String productExtraImagesDir = "../product-images/" + id + "/extras";
			String productImagesDir = "../product-images/" + id;
			
			FileUploadUtil.removeDir(productExtraImagesDir);
			FileUploadUtil.removeDir(productImagesDir);
			
			redirectAttributes.addFlashAttribute("message", 
					"The product ID " + id + " has been deleted successfully");
		} catch (ProductNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/products/edit/{id}")
	public String editProduct(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra, @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Product product = productService.get(id);
			List<Brand> listBrands = brandService.listAll();
			Integer numberOfExistingExtraImages = product.getImages().size();
			
			boolean isLoggedUserAdmin = false;
			if (loggedUser.hasRole("Admin")) {
				isLoggedUserAdmin = true;
			}
			
			boolean isReadOnlyForSalesperson = false;
			if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Editor")) {
				if (loggedUser.hasRole("Salesperson")) {
					isReadOnlyForSalesperson = true;
				}
			}
			isReadOnlyForSalesperson = false;
			
			List<City> listCities = cityService.listAll();
			
			model.addAttribute("isReadOnlyForSalesperson", isReadOnlyForSalesperson);
			model.addAttribute("isLoggedUserAdmin", isLoggedUserAdmin);
			model.addAttribute("product", product);
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
			model.addAttribute("numberOfExistingExtraImages", numberOfExistingExtraImages);
			model.addAttribute("listCities", listCities);
			
			return "products/product_form";
			
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/products/detail/{id}")
	public String viewProductDetails(@PathVariable("id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Product product = productService.get(id);			
			model.addAttribute("product", product);		
			
			return "products/product_detail_modal";
			
		} catch (ProductNotFoundException e) {
			ra.addFlashAttribute("message", e.getMessage());
			
			return defaultRedirectURL;
		}
	}	
}
