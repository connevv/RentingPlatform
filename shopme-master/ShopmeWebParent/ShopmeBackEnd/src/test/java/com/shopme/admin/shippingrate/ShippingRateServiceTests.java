package com.shopme.admin.shippingrate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class ShippingRateServiceTests {

	@MockBean private ShippingRateRepository shipRepo;
	@MockBean private ProductRepository productRepo;
	
	@InjectMocks
	private ShippingRateService shipService;
	
	@Test
	public void testCalculateShippingCost_NoRateFound() {
		Integer productId = 1;
		String city = "ABCDE";
		
		Mockito.when(shipRepo.findByCity(city)).thenReturn(null);
		
		assertThrows(ShippingRateNotFoundException.class, new Executable() {
			
			@Override
			public void execute() throws Throwable {
				shipService.calculateShippingCost(productId, city);
			}
		});
	}
	
	@Test
	public void testCalculateShippingCost_RateFound() throws ShippingRateNotFoundException {
		Integer productId = 1;
		Integer countryId = 234;
		String city = "Kichevo";
		
		ShippingRate shippingRate = new ShippingRate();
		shippingRate.setRate(10);
		
		Mockito.when(shipRepo.findByCity(city)).thenReturn(shippingRate);
		
		Product product = new Product();
		
		Mockito.when(productRepo.findById(productId)).thenReturn(Optional.of(product));
	
		float shippingCost = shipService.calculateShippingCost(productId, city);
		
		assertEquals(50, shippingCost);
	}
}
