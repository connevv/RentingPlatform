package com.shopme.product;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.product.Product;
import com.shopme.common.exception.ProductNotFoundException;

@Service
@Transactional
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULTS_PER_PAGE = 10;
	
	@Autowired private ProductRepository repo;
	
	public Page<Product> listByCategory(int pageNum, Integer categoryId) {
		String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return repo.listByCategory(categoryId, categoryIdMatch, pageable);
	}
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException("Could not find any product with alias " + alias);
		}
		
		return product;
	}
	
	public Page<Product> search(String keyword, int pageNum) {
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return repo.search(keyword, pageable);
		
	}
	
	public Page<Product> searchByCity(String city, int pageNum) {
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULTS_PER_PAGE);
		return repo.findProductsByCity(city, pageable);
		
	}
	
	public void updateVote(Integer id, String voteType, int vote) {
		if (voteType.equals("voteUp")) {
			repo.updateVotesUpStatus(id, vote + 1);
		} else if (voteType.equals("voteDown")) {
			repo.updateVotesDownStatus(id, vote + 1);
		}
	}
	
	public Product getProductById(Integer id) throws ProductNotFoundException{
		Optional<Product> product = repo.findById(id);
		return product.get();
	}
}
