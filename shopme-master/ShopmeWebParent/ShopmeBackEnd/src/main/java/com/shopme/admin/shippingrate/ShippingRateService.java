package com.shopme.admin.shippingrate;

import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.product.ProductRepository;
import com.shopme.common.entity.ShippingRate;
import com.shopme.common.entity.product.Product;

@Service
@Transactional
public class ShippingRateService {
	public static final int RATES_PER_PAGE = 10;
	private static final int DIM_DIVISOR = 139;	
	@Autowired private ShippingRateRepository shipRepo;
	@Autowired private ProductRepository productRepo;
	
	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, RATES_PER_PAGE, shipRepo);
	}	
	
	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate rateInDB = shipRepo.findByCity(
				rateInForm.getCity());
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);
		
		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
						 + "Bulgaria, " + rateInForm.getCity());				
		}
		shipRepo.save(rateInForm);
	}

	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return shipRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
	}
	
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
		
		shipRepo.updateCODSupport(id, codSupported);
	}
	
	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
			
		}
		shipRepo.deleteById(id);
	}
	
	public float calculateShippingCost(Integer productId, String city) 
			throws ShippingRateNotFoundException {
		ShippingRate shippingRate = shipRepo.findByCity(city);
		
		if (shippingRate == null) {
			throw new ShippingRateNotFoundException("No shipping rate found for the given "
					+ "destination. You have to enter shipping cost manually.");
		}
		
		Product product = productRepo.findById(productId).get();
				
		return 30 * shippingRate.getRate();
	}
}
