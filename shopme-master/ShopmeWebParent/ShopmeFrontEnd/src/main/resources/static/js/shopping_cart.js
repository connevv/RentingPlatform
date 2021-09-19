decimalSeparator = '.'
thousandsSeparator = ','

$(document).ready(function() {
	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		decreaseQuantity($(this));
	});
	
	$(".linkMinusDays").on("click", function(evt) {
		evt.preventDefault();
		decreaseDaysQuantity($(this));
	});
	
	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		increaseQuantity($(this));
	});
	
	$(".linkPlusDays").on("click", function(evt) {
		evt.preventDefault();
		increaseDaysQuantity($(this));
	});
	
	$(".linkRemove").on("click", function(evt) {
		evt.preventDefault();
		removeProduct($(this));
	});		
});

function decreaseQuantity(link) {
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId);
	newQuantity = parseInt(quantityInput.val()) - 1;
	daysQuantityInput = $("#daysQuantity" + productId).val();
	if (newQuantity > 0) {
		quantityInput.val(newQuantity);
		updateQuantity(productId, newQuantity, daysQuantityInput);
	} else {
		showWarningModal('Minimum quantity is 1');
	}	
}

function decreaseDaysQuantity(link) {
	productId = link.attr("pid");
	quantityInput = $("#quantity" + productId).val();
	daysQuantityInput = $("#daysQuantity" + productId);
	newQuantity = parseInt(daysQuantityInput.val()) - 1;
	
	if (newQuantity > 0) {
		daysQuantityInput.val(newQuantity);
		updateQuantity(productId, quantityInput, newQuantity);
	} else {
		showWarningModal('Minimum quantity is 1');
	}	
}

function increaseQuantity(link) {
		productId = link.attr("pid");
		quantityInput = $("#quantity" + productId);
		newQuantity = parseInt(quantityInput.val()) + 1;
		
		daysQuantityInput = $("#daysQuantity" + productId).val();
		
		if (newQuantity <= 5) {
			quantityInput.val(newQuantity);
			updateQuantity(productId, newQuantity, daysQuantityInput);
		} else {
			showWarningModal('Maximum quantity is 5');
		}	
}

function increaseDaysQuantity(link) {
		productId = link.attr("pid");
		quantityInput = $("#quantity" + productId).val();
		daysQuantityInput = $("#daysQuantity" + productId);
		newQuantity = parseInt(daysQuantityInput.val()) + 1;
		
		if (newQuantity <= 5) {
			daysQuantityInput.val(newQuantity);
			updateQuantity(productId, quantityInput, newQuantity);
		} else {
			showWarningModal('Maximum quantity is 5');
		}	
}

function updateQuantity(productId, quantity, daysQuantity) {
	url = contextPath + "cart/update/" + productId + "/" + quantity + "/" + daysQuantity;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(updatedSubtotal) {
		updateSubtotal(updatedSubtotal, productId);
		updateTotal();
	}).fail(function() {
		showErrorModal("Error while updating product quantity.");
	});	
}

function updateSubtotal(updatedSubtotal, productId) {
	$("#subtotal" + productId).text(formatCurrency(updatedSubtotal));
}

function updateTotal() {
	total = 0.0;
	productCount = 0;
	
	$(".subtotal").each(function(index, element) {
		productCount++;
		total += parseFloat(clearCurrencyFormat(element.innerHTML));
	});
	
	if (productCount < 1) {
		showEmptyShoppingCart();
	} else {
		$("#total").text(formatCurrency(total));		
	}
	
}

function showEmptyShoppingCart() {
	$("#sectionTotal").hide();
	$("#sectionEmptyCartMessage").removeClass("d-none");
}

function removeProduct(link) {
	url = link.attr("href");

	$.ajax({
		type: "DELETE",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		rowNumber = link.attr("rowNumber");
		removeProductHTML(rowNumber);
		updateTotal();
		updateCountNumbers();
		
		showModalDialog("Shopping Cart", response);
		
	}).fail(function() {
		showErrorModal("Error while removing product.");
	});				
}

function removeProductHTML(rowNumber) {
	$("#row" + rowNumber).remove();
	$("#blankLine" + rowNumber).remove();
}

function updateCountNumbers() {
	$(".divCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	}); 
}


function formatCurrency(amount) {
	return $.number(amount, 2, decimalSeparator, thousandsSeparator);
}

function clearCurrencyFormat(numberString) {
	result = numberString.replaceAll(thousandsSeparator, "");
	return result.replaceAll(decimalSeparator, ".");
}