function clearFilter() {
	window.location = moduleURL;	
}

function showDeleteConfirmModal(link, entityName) {
	entityId = link.attr("entityId");
	
	$("#yesButton").attr("href", link.attr("href"));	
	$("#confirmText").text("Сигурни ли сте че искате да изтриете поръчка с номер "
							 + entityId + "?");
	$("#confirmModal").modal();	
}