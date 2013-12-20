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
	   	colNames:['code','name','days', 'sh'],
	   	colModel:[
	   		{name:'code',index:'code', width:200, align: 'center'},
	   		{name:'name',index:'name', width:200, align: 'center'},
	   		{name:'days',index:'days', width:200, align: 'center'},
	   		{name:'sh',index:'sh', width:500, align: 'center'},
	   	],
	   	rowNum:20,
	   	rowList:[10,20,30],
	   	pager: '#pageCount',
	   	sortname: 'days',
	    viewrecords: true,
	    sortorder: "desc",
	    height: '100%',
	    caption: "Match Result",
	    ondblClickRow: function (rowid, iRow, iCol, e) {
            var rowData = $("#stockContent").jqGrid('getRowData', rowid);
            var realImgUrl = "http://image.sinajs.cn/newchart/min/n/" + rowData.code + ".gif";
            var dailyKImgUrl = "http://image.sinajs.cn/newchart/daily/n/" + rowData.code + ".gif";
            $("#realImg").attr('src', realImgUrl);
            $("#dailyKImg").attr('src', dailyKImgUrl);
            $.blockUI({ 
                message: $('#box'), 
                css: { 
                    border: 'none', 
                    backgroundColor: '#fff', 
                    '-webkit-border-radius': '10px', 
                    '-moz-border-radius': '10px', 
                    color: '#000',
                    top:  '100px',
                    left: '400px',
                    width: '700px',
                    cursor: 'arrow'
                }
            });
        	$('.blockOverlay').attr('title','单击关闭').click($.unblockUI);
        }
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
