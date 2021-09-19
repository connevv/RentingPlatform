package com.shopme.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTests {

	@Autowired ProductRepository repo;
	
	@Test
	public void testFindByAlias() {
		String alias = "canon-eos-m50";
		Product product = repo.findByAlias(alias);
		
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testFindByCity() {
		Page<Product> product = repo.findProductsByCity("Sofia", null);
		
		assertEquals(product.getContent().size(), 3);
	}
}
