$(function() {
	$("input[type=button]").button();
	
	//radio button
	radioDisplay("transfer", "real", "#transferDate");
	radioDisplay("calculate", "real", "#calculateDate");
	radioDisplay("oneClickCalculate", "real", "#oneClickCalculateDate");
	
	//date picker
	var date = new Date();
	if(date.getHours() > 15) {	//after 15:00 could choose the day
		getRangeDatePicker("#transferStartDate", "#transferEndDate", "0");
		getRangeDatePicker("#calculateStartDate", "#calculateEndDate", "0");
		getRangeDatePicker("#oneClickCalculateStartDate", "#oneClickCalculateEndDate", "0");
	}
	else {
		getRangeDatePicker("#transferStartDate", "#transferEndDate", "-1d");
		getRangeDatePicker("#calculateStartDate", "#calculateEndDate", "-1d");
		getRangeDatePicker("#oneClickCalculateStartDate", "#oneClickCalculateEndDate", "-1d");

		date.setDate(date.getDate() - 1);
	}
	//set default date
	$("#transferStartDate").val(date.toString("yyyy-MM-dd"));
	$("#transferEndDate").val(date.toString("yyyy-MM-dd"));
	$("#calculateStartDate").val(date.toString("yyyy-MM-dd"));
	$("#calculateEndDate").val(date.toString("yyyy-MM-dd"));
	$("#oneClickCalculateStartDate").val(date.toString("yyyy-MM-dd"));
	$("#oneClickCalculateEndDate").val(date.toString("yyyy-MM-dd"));
	
	//get strategys
	getAjaxRequest("rs/stock/strategy", function(data){
		for(var i in data) {
			$("#calc_strategy").append("<input type='checkbox' id='"+data[i]+"' value='"+data[i]+"' checked='checked' /><label for='"+data[i]+"'>"+data[i]+"</label>");
		}
	});
	
	$("#retrieve").button().click(function(event) {
    	event.preventDefault();
    	var retrieveRadio = $("input[name='retrieve']:checked").val();
    	getAjaxRequest("rs/task/retrieve?type=" + retrieveRadio, function(data){
    		alert(data);
    	});
    });
	$("#retrieveSC").button().click(function(event) {
    	event.preventDefault();
    	prograssbarTemplate("#retrieveProgressbar");
    	progress("#retrieveProgressbar", 1 * 1000);
    	getAjaxRequest("rs/task/retrieve?type=sc", function(data){
    		progress("#retrieveProgressbar", 1 * 1000);
    		setTimeout(function(){
    			destroyProgressBar("#retrieveProgressbar");
    		}, 2000);
    	});
    });
	$("#retrieveDDX").button().click(function(event) {
    	event.preventDefault();
    	getAjaxRequest("rs/task/retrieve?type=ddx", function(data){
    		alert(data);
    	});
    });
	
	$("#transfer").button().click(function(event) {
    	event.preventDefault();
    	var transferRadio = $("input[name='transfer']:checked").val();
    	var startDate;
    	var endDate;
    	if(transferRadio == "history") {
    		startDate = $("#transferStartDate").val();
    		endDate = $("#transferEndDate").val();
    	}
    	else if(transferRadio == "real") {
    		var d = new Date();
    		startDate = d.toString("yyyy-MM-dd");
    		endDate = d.toString("yyyy-MM-dd");
    	}
    	var url = "rs/task/transfer?type=" + transferRadio + "&startDate=" + startDate + "&endDate=" + endDate;
    	getAjaxRequest(url, function(data){
    		alert(data);
    	});
    });
	
	$("#calculate").button().click(function(event) {
    	event.preventDefault();
    	var calculateRadio = $("input[name='calculate']:checked").val();
    	var startDate;
    	var endDate;
    	if(calculateRadio == "history") {
    		startDate = $("#calculateStartDate").val();
    		endDate = $("#calculateEndDate").val();
    	}
    	else if(calculateRadio == "real") {
    		var d = new Date();
    		startDate = d.toString("yyyy-MM-dd");
    		endDate = d.toString("yyyy-MM-dd");
    	}
		var strategys = "";
		$("#calc_strategy input[type='checkbox']:checked").each(function(){
			strategys += $(this).val() + ",";
		});
    	var url = "rs/task/calculate?type=" + calculateRadio + "&startDate=" + startDate + "&endDate=" + endDate 
    				+ "&strategys=" + strategys.substring(0, strategys.length - 1);
    	getAjaxRequest(url, function(data){
    		alert(data);
    	});
    });
	
	$("#oneClickCalculate").button().click(function(event) {
    	event.preventDefault();
    	var oneClickCalculateRadio = $("input[name='oneClickCalculate']:checked").val();
    	var startDate;
    	var endDate;
    	if(oneClickCalculateRadio == "history") {
    		startDate = $("#oneClickCalculateStartDate").val();
    		endDate = $("#oneClickCalculateEndDate").val();
    	}
    	else if(oneClickCalculateRadio == "real") {
    		var d = new Date();
    		startDate = d.toString("yyyy-MM-dd");
    		endDate = d.toString("yyyy-MM-dd");
    	}
    	var url = "rs/task/oneClickCalculate?type=" + oneClickCalculateRadio + "&startDate=" + startDate + "&endDate=" + endDate;
    	getAjaxRequest(url, function(data){
    		alert(data);
    	});
    });
});

function radioDisplay(item, value, displayId) {
	$("input[name='"+item+"']").change(function(){
		var radioValue = $("input[name='"+item+"']:checked").val();
		if(radioValue == value) {
			$(displayId).hide();
		}
		else {
			$(displayId).show();
		}
	});
}

function prograssbarTemplate(itemId) {
	var progressbar = $( itemId );
	var labelId = itemId.substring(1, itemId.length) + '-label';
	var progressLabel = $( "#" + labelId );
	progressbar.progressbar({
		value : false,
		change : function() {
			progressLabel.text(progressbar.progressbar("value") + "%");
		},
		complete : function() {
			progressLabel.text("Complete!");
		}
	});
}

function destroyProgressBar(itemId){
	var progressbar = $( itemId );
	var labelId = itemId.substring(1, itemId.length) + '-label';
	var progressLabel = $( "#" + labelId );
	progressbar.progressbar( "destroy" );
	progressLabel.text("");
}

function progress(itemId, time) {
	var progressbar = $( itemId );
	var val = progressbar.progressbar("value") || 0;
	progressbar.progressbar("value", val + 1);
	if (val < 98) {
		setTimeout(function(){
			progress(itemId, time);
		}, time);
	}
}