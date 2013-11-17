function getAjaxRequest(requestUrl, onSucessFunction) {
	$.ajax({
		url: requestUrl,
		type: 'GET',
		dataType: 'json',
		cache: false,
		async: true,
		success: onSucessFunction
	});
}

function postAjaxRequest(requestUrl, onSucessFunction) {
	$.ajax({
		url: requestUrl,
		type: 'POST',
		dataType: 'json',
		cache: false,
		async: false,
		success: onSucessFunction
	});
}

function formatDate(datetime) {
	var date = new Date(Number(datetime));
	return date.toString("yyyy-MM-dd HH:mm:ss");
}

function getRangeDatePicker(startId, endId, limitDate) {
	$(startId).datepicker({
		defaultDate : '-1w',
		changeMonth : true,
		numberOfMonths : 2,
		dateFormat : 'yy-mm-dd',
		maxDate: '0',
		onClose : function(selectedDate) {
			$(endId).datepicker("option", "minDate", selectedDate);
		}
	});
	$(endId).datepicker({
		defaultDate : "+1w",
		changeMonth : true,
		numberOfMonths : 2,
		dateFormat : 'yy-mm-dd',
		maxDate: limitDate,
		onClose : function(selectedDate) {
			$(startId).datepicker("option", "maxDate", selectedDate);
		}
	});
	
}