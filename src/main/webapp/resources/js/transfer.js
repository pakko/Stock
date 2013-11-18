$(function() {
	//date picker
	getRangeDatePicker("#startDatepicker", "#endDatepicker", "0");
	
	$("#searchStock").button().click(function(event) {
    	event.preventDefault();
    	var code = $("#code").val();
    	var startDate = $("#startDatepicker").val();
    	var endDate = $("#endDatepicker").val();
    	var url = 'rs/transfer?code=' + code + '&startDate=' + startDate + '&endDate=' + endDate;
    	$("#stockContent").jqGrid().setGridParam({url : url}).trigger("reloadGrid");
    });
	
	//for stock code auto complete
	var availableStockCodes = new Array();
	getAjaxRequest("rs/stock/code", false, function(data){
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
	   	url:'rs/transfer?code=' + code,
		datatype: "json",
		mtype: 'POST',
		contentType: 'application/json',
	   	colNames:['code','date','nowPrice','ltp',
	   	          'ma5','ma10','ma20','ma30','ma60','ma120','ma250',
	   	          'hsl5','hsl10','hsl20','hsl30','hsl60','hsl120','hsl250',
	   	          'up5','up10','up20','up30','up60','up120','up250', 'isReal'],
	   	colModel:[
	   		{name:'code',index:'code', width:80, align: 'center'},
	   		{name:'date',index:'date', width:90, align: 'center', formatter: 'date', formatoptions: {srcformat:'u',newformat:'Y-m-d'}},
	   		{name:'nowPrice',index:'nowPrice', width:35, align: 'center'},
	   		{name:'ltp',index:'ltp', width:35, align: 'center'},
	   		
	   		{name:'ma5',index:'ma5', width:35, align: 'center'},
	   		{name:'ma10',index:'ma10', width:35, align: 'center'},
	   		{name:'ma20',index:'ma20', width:35, align: 'center'},
	   		{name:'ma30',index:'ma30', width:35, align: 'center'},
	   		{name:'ma60',index:'ma60', width:35, align: 'center'},
	   		{name:'ma120',index:'ma120', width:35, align: 'center'},
	   		{name:'ma250',index:'ma250', width:35, align: 'center'},
	   		
	   		{name:'hsl5',index:'hsl5', width:35, align: 'center'},
	   		{name:'hsl10',index:'hsl10', width:35, align: 'center'},
	   		{name:'hsl20',index:'hsl20', width:35, align: 'center'},
	   		{name:'hsl30',index:'hsl30', width:35, align: 'center'},
	   		{name:'hsl60',index:'hsl60', width:35, align: 'center'},
	   		{name:'hsl120',index:'hsl120', width:35, align: 'center'},
	   		{name:'hsl250',index:'hsl250', width:35, align: 'center'},
	   		
	   		{name:'up5',index:'up5', width:35, align: 'center'},
	   		{name:'up10',index:'up10', width:35, align: 'center'},
	   		{name:'up20',index:'up20', width:35, align: 'center'},
	   		{name:'up30',index:'up30', width:35, align: 'center'},
	   		{name:'up60',index:'up60', width:35, align: 'center'},
	   		{name:'up120',index:'up120', width:35, align: 'center'},
	   		{name:'up250',index:'up250', width:35, align: 'center'},
	   		
	   		{name:'isReal',index:'isReal', width:35, align: 'center'}
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
