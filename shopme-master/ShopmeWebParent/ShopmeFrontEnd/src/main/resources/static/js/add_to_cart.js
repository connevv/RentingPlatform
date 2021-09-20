$(document).ready(function() {
	$("#buttonAdd2Cart").on("click", function(evt) {
		addToCart();
	});
});

function addToCart() {
	startDay = $("#startDate").val();
	endDay = $("#endDate").val();
	url = contextPath + "cart/add/" + productId + "/" + startDay + "/" + endDay;
	
	$.ajax({
		type: "POST",
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(response) {
		showModalDialog("Количка", response);
	}).fail(function() {
		showErrorModal("Грешка при добавяне на продукт в количката.");
	});
}