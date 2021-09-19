package com.shopme.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Comment;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CommentRepositoryTests {
	@Autowired CommentRepository commentRepository;
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testAddComment() {
		Product product = entityManager.find(Product.class, 70);
		Customer customer = entityManager.find(Customer.class, 5);
		Comment comment = new Comment("This product is awsome", product, customer, new Date());
		
		Comment savedComment = commentRepository.save(comment);
		assertThat(savedComment).isNotNull();
		assertThat(savedComment.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListComments() {
		List<Comment> allComments = commentRepository.findAll();
		
		assertEquals(allComments.size(), 1);
	}
	
	@Test
	public void testGetCommentsByProductAlias() {
		String productAlias = "ASUS-TUF-Gaming-A15-Gaming-Laptop";
		Page<Comment> allComments = commentRepository.findCommentsByProductAlias(productAlias, null);
		
		assertEquals(allComments.getContent().size(), 1);
	}
}
