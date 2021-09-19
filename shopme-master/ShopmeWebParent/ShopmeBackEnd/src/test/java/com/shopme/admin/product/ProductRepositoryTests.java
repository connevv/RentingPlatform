package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.City;
import com.shopme.common.entity.User;
import com.shopme.common.entity.product.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		Brand brand = entityManager.find(Brand.class, 37);
		Category category = entityManager.find(Category.class, 5);
		User user = entityManager.find(User.class, 5);
		City city = entityManager.find(City.class, 1);
		
		Product product = new Product();
		product.setName("pick hose");
		product.setAlias("pick_hose");
		product.setShortDescription("Short description for Acer Aspire");
		product.setFullDescription("Full description for Acer Aspire");
		
		product.setBrand(brand);
		product.setCategory(category);
		product.setMainImage("test");
		
		Calendar cal = Calendar.getInstance();
	    cal.add(Calendar.DATE, -1);
		
		product.setPrice(678);
		product.setEnabled(true);
		
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		product.setVotesDown(0);
		product.setVotesUp(1);
		
		product.setUser(user);
		product.setCity(city);
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllProducts() {
		Iterable<Product> iterableProducts = repo.findAll();
		
		iterableProducts.forEach(System.out::println);
	}
	
	@Test
	public void testGetProduct() {
		Integer id = 2;
		Product product = repo.findById(id).get();
		System.out.println(product);
		
		assertThat(product).isNotNull();
	}
	
	@Test
	public void testUpdateProduct() {
		Integer id = 1;
		Product product = repo.findById(id).get();
		product.setPrice(499);
		
		repo.save(product);
		
		Product updatedProduct = entityManager.find(Product.class, id);
		
		assertThat(updatedProduct.getPrice()).isEqualTo(499);
	}
	
	@Test
	public void testDeleteProduct() {
		Integer id = 3;
		repo.deleteById(id);
		
		Optional<Product> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
	
	@Test
	public void testSaveProductWithImages() {
		Integer productId = 1;
		Product product = repo.findById(productId).get();
		
		product.setMainImage("main image.jpg");
		product.addExtraImage("extra image 1.png");
		product.addExtraImage("extra_image_2.png");
		product.addExtraImage("extra-image3.png");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getImages().size()).isEqualTo(3);
	}
	
	@Test
	public void testSaveProductWithDetails() {
		Integer productId = 70;
		Product product = repo.findById(productId).get();
		
		product.addDetail("Device Memory", "128 GB");
		product.addDetail("CPU Model", "MediaTek");
		product.addDetail("OS", "Android 10");
		City city = entityManager.find(City.class, 1);
		product.setCity(city);
		
		Product savedProduct = repo.save(product);
		assertThat(savedProduct.getDetails()).isNotEmpty();		
	}
	
	@Test
	public void testFindByUserId() {
		Integer userId = 1;
		Page<Product> products = repo.findProductsByUserId(userId, null);
		
		assertThat(products.getSize()).isGreaterThan(0);
	}
	
	@Test
	public void testFindByCity() {
		String city = "Sofia";
		Page<Product> products = repo.findProductsByCity(city, null);
		
		assertThat(products.getSize()).isGreaterThan(0);
	}
}
