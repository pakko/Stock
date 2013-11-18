function getAjaxRequest(requestUrl, async, onSucessFunction) {
	$.ajax({
		url: requestUrl,
		type: 'GET',
		dataType: 'json',
		cache: false,
		async: async,
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

function getDaysBetweenDates(d0, d1) {

	  var msPerDay = 8.64e7;

	  // Copy dates so don't mess them up
	  var x0 = new Date(d0);
	  var x1 = new Date(d1);

	  // Set to noon - avoid DST errors
	  x0.setHours(12,0,0);
	  x1.setHours(12,0,0);

	  // Round to remove daylight saving errors
	  return Math.round( (x1 - x0) / msPerDay ) + 1;
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