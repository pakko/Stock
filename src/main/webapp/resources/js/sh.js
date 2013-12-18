$(function() {
	//grid
	var result_data;
	postAjaxRequest("rs/result/sh", function(data){
		result_data = data;
	});
	$("#stockContent").jqGrid({
		data: result_data,
		datatype: "local",
		mtype: 'POST',
		contentType: 'application/json',
	   	colNames:['code','name','days'],
	   	colModel:[
	   		{name:'code',index:'code', width:200, align: 'center'},
	   		{name:'name',index:'name', width:200, align: 'center'},
	   		{name:'days',index:'days', width:200, align: 'center'},
	   	],
	   	rowNum:20,
	   	rowList:[10,20,30],
	   	pager: '#pageCount',
	   	sortname: 'days',
	    viewrecords: true,
	    sortorder: "desc",
	    height: '100%',
	    caption: "Match Result"
	});
	$("#stockContent").jqGrid('navGrid','#pageCount',{edit:false,add:false,del:false,refresh:false});
	$("#stockContent").jqGrid('navButtonAdd', "#pageCount", {
	     caption: "", title: "Reload Grid", buttonicon: "ui-icon-refresh",
	     onClickButton: function () {
	     	postAjaxRequest("rs/result/sh", function(data){
	     		$("#stockContent").jqGrid('clearGridData')
	     			.jqGrid('setGridParam', {data: data}).trigger('reloadGrid');
	     	});
	     }
	});
	
	
});
