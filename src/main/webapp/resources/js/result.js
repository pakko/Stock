$(function() {
	//date picker
	getRangeDatePicker("#startDatepicker", "#endDatepicker", "0");
	
	$("#searchStock").button().click(function(event) {
    	event.preventDefault();
    	var code = $("#code").val();
    	var startDate = $("#startDatepicker").val();
    	var endDate = $("#endDatepicker").val();
    	
    	var url = 'rs/result?code=' + code + '&startDate=' + startDate + '&endDate=' + endDate;
    	postAjaxRequest(url, function(data){
    		$("#stockContent").jqGrid('clearGridData')
    			.jqGrid('setGridParam', {data: data}).trigger('reloadGrid');
    	});
    });
	
	//grid
	var result_data;
	/*postAjaxRequest("rs/result", function(data){
		result_data = data;
	});*/
	$("#stockContent").jqGrid({
		data: result_data,
		datatype: "local",
		mtype: 'POST',
		contentType: 'application/json',
	   	colNames:['code','name','date','strategy', 'ddx', 'd5', 'd10', 'dnow', 'sh'],
	   	colModel:[
	   		{name:'code',index:'code', width:150, align: 'center'},
	   		{name:'name',index:'name', width:150, align: 'center'},
	   		{name:'date',index:'date', width:150, align: 'center', formatter: 'date', formatoptions: {srcformat:'u',newformat:'Y-m-d'}},
	   		{name:'strategy',index:'strategy', width:150, align: 'center'},
	   		{name:'ddx',index:'ddx', width:100, align: 'center'},
	   		{name:'d5',index:'d5', width:100, align: 'center'},
	   		{name:'d10',index:'d10', width:100, align: 'center'},
	   		{name:'dnow',index:'dnow', width:100, align: 'center'},
	   		{name:'sh',index:'sh', width:100, align: 'center'}
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
	   		groupField : ['date'],
	   		groupOrder : ['desc'],
	   		groupText : ['<b>{0} - {1} Item(s)</b>'],
	   		groupDataSorted : true
	   	},
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
        },
        loadComplete: function(data) {
        	if(data == null){
        		return;
        	}
        	var rowData = data.rows;
        	for(var i=0; i<rowData.length;i++){
        		var v = rowData[i];
        		colorShow(i, v.ddx, 'ddx', 100);
        		colorShow(i, v.d5, 'd5', 100);
        		colorShow(i, v.d10, 'd10', 100);
        		colorShow(i, v.dnow, 'dnow', 100);
        	}
        }
	});
	$("#stockContent").jqGrid('navGrid','#pageCount',{edit:false,add:false,del:false,refresh:false});
	$("#stockContent").jqGrid('navButtonAdd', "#pageCount", {
	     caption: "", title: "Reload Grid", buttonicon: "ui-icon-refresh",
	     onClickButton: function () {
	     	postAjaxRequest("rs/result", function(data){
	     		$("#stockContent").jqGrid('clearGridData')
	     			.jqGrid('setGridParam', {data: data}).trigger('reloadGrid');
	     	});
	     }
	});
	
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
	
	function colorShow(index, data, field, digit) {
		if(data < 0){
			$("#stockContent").jqGrid('setCell', index+1, field, Math.round(data*digit)/digit, {'background':'url(\'resources/css/images/bg-rowGreen.gif\') repeat-x'});
		}
		else if(data > 0) {
			$("#stockContent").jqGrid('setCell', index+1, field, Math.round(data*digit)/digit, {'background':'url(\'resources/css/images/bg-rowRed.gif\') repeat-x'});
		}
		else {
			$("#stockContent").jqGrid('setCell', index+1, field, Math.round(data*digit)/digit, "");
		}
	}
	
});
