package com.shopme.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.product.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

	@Query("SELECT p FROM Product p WHERE p.enabled = true "
			+ "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
			+ " ORDER BY p.name ASC")
	public Page<Product> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);
	
	public Product findByAlias(String alias);
	
	@Query(value = "SELECT * FROM products WHERE enabled = true AND "
			+ "MATCH(name, short_description, full_description) AGAINST (?1)", 
			nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Product p WHERE p.enabled = true AND p.city.name = ?1")
	public Page<Product> findProductsByCity(String city, Pageable pageable);
	
	@Query("UPDATE Product p SET p.votesUp = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateVotesUpStatus(int id, int vote);
	
	@Query("UPDATE Product p SET p.votesDown = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateVotesDownStatus(int id, int vote);
}
