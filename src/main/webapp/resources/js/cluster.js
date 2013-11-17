$(function() {
	//date picker
	getRangeDatePicker("#startDatepicker", "#endDatepicker", "0");
	
	$("#searchStock").button().click(function(event) {
    	event.preventDefault();
    	var code = $("#code").val();
    	var startDate = $("#startDatepicker").val();
    	var endDate = $("#endDatepicker").val();
    	var url = 'rs/stock?code=' + code + '&startDate=' + startDate + '&endDate=' + endDate;
    	$("#stockContent").jqGrid().setGridParam({url : url}).trigger("reloadGrid");
    });
	
	//for stock code auto complete
	var availableStockCodes = new Array();
	getAjaxRequest("rs/stock/code", function(data){
		for(var i in data) {
			var temp_obj = {
					value: data[i].code,
					desc: data[i].code + " " + data[i].name
			};
			availableStockCodes.push(temp_obj);
		}
	});
	$( "#code" ).autocomplete({
		source: availableStockCodes
	}).data( "ui-autocomplete" )._renderItem = function( ul, item ) {
		return $( "<li>" ).append( "<a>" + item.desc + "</a>" ).appendTo( ul );
	};
	
	//grid
	var code = 'sz002306';
	$("#stockContent").jqGrid({
	   	url:'rs/stock?code=' + code,
		datatype: "json",
		mtype: 'POST',
		contentType: 'application/json',
	   	colNames:['code','date','opening','max','close','min','tradeVolume','change','changeRate','ma5','ma10','ma20','turnOverRate'],
	   	colModel:[
	   		{name:'code',index:'code', width:80, align: 'center'},
	   		{name:'date',index:'date', width:90, align: 'center', formatter: 'date', formatoptions: {srcformat:'u',newformat:'Y-m-d'}},
	   		{name:'opening',index:'opening', width:65, align: 'center'},
	   		{name:'max',index:'max', width:65, align: 'center'},
	   		{name:'close',index:'close', width:65, align: 'center'},
	   		{name:'min',index:'min', width:65, align: 'center'},
	   		{name:'tradeVolume',index:'tradeVolume', width:80, align: 'center'},
	   		{name:'change',index:'change', width:65, align: 'center'},
	   		{name:'changeRate',index:'changeRate', width:80, align: 'center'},
	   		{name:'ma5',index:'ma5', width:65, align: 'center'},
	   		{name:'ma10',index:'ma10', width:65, align: 'center'},
	   		{name:'ma20',index:'ma20', width:65, align: 'center'},
	   		{name:'turnOverRate',index:'turnOverRate', width:80, align: 'center'}
	   	],
	   	rowNum:20,
	   	rowList:[10,20,30],
	   	pager: '#pageCount',
	   	sortname: 'date',
	    viewrecords: true,
	    sortorder: "desc",
	    height: '100%',
	    caption: code
	});
	$("#stockContent").jqGrid('navGrid','#pageCount',{edit:false,add:false,del:false});
	
});
