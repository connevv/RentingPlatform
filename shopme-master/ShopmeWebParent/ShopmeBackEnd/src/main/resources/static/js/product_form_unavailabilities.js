$(document).ready(function() {		
	$("a[name='linkRemoveUnavailability']").each(function(index) {
		$(this).click(function() {
			removeUnavailabilitySectionByIndex(index);
		});
	});
	
});

function addNextUnavailabilitySection() {
	allDivDetails = $("[id^='divUnavailability']");
	divDetailsCount = allDivDetails.length;
	
	htmlDetailSection = `
		<div class="form-inline" id="divUnavailability${divDetailsCount}">
			<label class="m-3">Start date:</label>
			<input type="date" class="form-control w-25" name="unavailabilityStartDate" />
			<label class="m-3">End date:</label>
			<input type="date" class="form-control w-25" name="unavailabilityEndDate" />
		</div>	
	`;
	
	$("#divProductUnavailability").append(htmlDetailSection);

	previousDivDetailSection = allDivDetails.last();
	previousDivDetailID = previousDivDetailSection.attr("id");
	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x icon-dark"
			href="javascript:removeDetailSectionById('${previousDivDetailID}')"
			title="Remove this detail"></a>
	`;
	
	previousDivDetailSection.append(htmlLinkRemove);
	
}

function removeDetailSectionById(id) {
	$("#" + id).remove();
}

function removeUnavailabilitySectionByIndex(index) {
	$("#divUnavailability" + index).remove();
}