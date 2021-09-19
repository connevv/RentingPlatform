package com.shopme.comment;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Comment;
import com.shopme.common.entity.product.Product;

@Service
@Transactional
public class CommentService {
	public static final int PRODUCTS_PER_PAGE = 5;
	
	@Autowired CommentRepository commentRepository;
	
	public void createComment(Comment comment) {
		commentRepository.save(comment);
	}
	
	public void getCommentsByProductsId(Product product, Pageable pageable) {
		commentRepository.findAll(product.getId(), pageable);
	}
	
	public Page<Comment> listByPage(int pageNum, Product product, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
		
		if (keyword != null) {
			return commentRepository.findAll(product.getId(), keyword, pageable);
		}
		
		//page = commentRepository.findAll(pageable);
		
		return commentRepository.findAll(product.getId(), pageable);
	}
}
