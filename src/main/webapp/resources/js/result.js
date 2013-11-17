$(function() {
	//date picker
	getRangeDatePicker("#startDatepicker", "#endDatepicker", "0");
	
	$("#searchStock").button().click(function(event) {
    	event.preventDefault();
    	var startDate = $("#startDatepicker").val();
    	var endDate = $("#endDatepicker").val();
    	
    	var url = 'rs/result?startDate=' + startDate + '&endDate=' + endDate;
    	postAjaxRequest(url, function(data){
    		$("#stockContent").jqGrid('clearGridData')
    			.jqGrid('setGridParam', {data: data}).trigger('reloadGrid');
    	});
    });
	
	//grid
	var result_data;
	postAjaxRequest("rs/result", function(data){
		result_data = data;
	});
	$("#stockContent").jqGrid({
		data: result_data,
		datatype: "local",
		mtype: 'POST',
		contentType: 'application/json',
	   	colNames:['code','name','date','strategy'],
	   	colModel:[
	   		{name:'code',index:'code', width:200, align: 'center'},
	   		{name:'name',index:'name', width:200, align: 'center'},
	   		{name:'date',index:'date', width:200, align: 'center', formatter: 'date', formatoptions: {srcformat:'u',newformat:'Y-m-d'}},
	   		{name:'strategy',index:'strategy', width:200, align: 'center'}
	   	],
	   	rowNum:20,
	   	rowList:[10,20,30],
	   	pager: '#pageCount',
	   	sortname: 'date',
	    viewrecords: true,
	    sortorder: "desc",
	    height: '100%',
	    caption: "Match Result",
	    grouping:true,
	   	groupingView : {
	   		groupField : ['code'],
	   		groupText : ['<b>{0} - {1} Item(s)</b>'],
	   		groupDataSorted : true
	   	}
	});
	$("#stockContent").jqGrid('navGrid','#pageCount',{edit:false,add:false,del:false});
	
	$("#chngroup").change(function(){
		var vl = $(this).val();
		if(vl) {
			if(vl == "clear") {
				$("#stockContent").jqGrid('groupingRemove',true);
			} else {
				$("#stockContent").jqGrid('groupingGroupBy',vl);
			}
		}
	});
});
