$(document).ready(function() {
	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		quantityInput = $("#quantity" + productId);
		newQuantity = parseInt(quantityInput.val()) - 1;
		
		if (newQuantity > 0) {
			quantityInput.val(newQuantity);
		} else {
			showWarningModal('Minimum quantity is 1');
		}
	});
	
	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		quantityInput = $("#quantity" + productId);
		newQuantity = parseInt(quantityInput.val()) + 1;
		
		daysQuantityInput = $("#daysQuantity" + productId);
		newDaysQuantity = parseInt(daysQuantityInput.val()) + 1;
		
		if (newQuantity <= 5) {
			quantityInput.val(newQuantity);
			
		} else {
			showWarningModal('Maximum quantity is 5');
		}	
	});
	
		$(".linkMinusDays").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		quantityInput = $("#daysQuantity" + productId);
		newQuantity = parseInt(quantityInput.val()) - 1;
		
		if (newQuantity > 0) {
			quantityInput.val(newQuantity);
		} else {
			showWarningModal('Minimum quantity is 1');
		}
	});
	
	$(".linkPlusDays").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		
		daysQuantityInput = $("#daysQuantity" + productId);
		newDaysQuantity = parseInt(daysQuantityInput.val()) + 1;
		
		if (newDaysQuantity <= 5) {
			daysQuantityInput.val(newDaysQuantity);
		} else {
			showWarningModal('Maximum quantity is 5');
		}
		
	});	
});