package com.shopme.comment;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Comment;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Integer> {
	@Query("SELECT NEW Comment(c.id, c.name, c.product, c.customer, c.date) FROM Comment c ORDER BY c.name ASC")
	public List<Comment> findAll();
	
	@Query("SELECT NEW Comment(c.id, c.name, c.product, c.customer, c.date) FROM Comment c ORDER BY c.name ASC")
	public List<Comment> findByProduct();
	
	@Query("SELECT c FROM Comment c WHERE c.product.id = ?1 AND c.customer.firstName LIKE %?2%")
	public Page<Comment> findAll(Integer userId, String keyword, Pageable pageable);
	
	@Query("SELECT c FROM Comment c WHERE c.product.id = ?1")
	public Page<Comment> findAll(Integer userId, Pageable pageable);
	
	@Query("SELECT c FROM Comment c WHERE c.customer.id = ?1")
	public Page<Comment> findCommentsByCusomerId(Integer userId, Pageable pageable);
	
	@Query("SELECT c FROM Comment c WHERE c.product.alias = ?1")
	public Page<Comment> findCommentsByProductAlias(String productAlias, Pageable pageable);
}
