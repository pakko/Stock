$(function() {
	//date picker
	getRangeDatePicker("#startDatepicker", "#endDatepicker", "0");
	
	$("#searchStock").button().click(function(event) {
    	event.preventDefault();
    	var code = $("#code").val();
    	var startDate = $("#startDatepicker").val();
    	var endDate = $("#endDatepicker").val();
    	
    	var url = 'rs/analyzer?code=' + code + '&startDate=' + startDate + '&endDate=' + endDate;
    	postAjaxRequest(url, function(data){
    		$("#stockContent").jqGrid('clearGridData')
    			.jqGrid('setGridParam', {data: data}).trigger('reloadGrid');
    	});
    });
	
	//grid
	var result_data;
	postAjaxRequest("rs/analyzer", function(data){
		result_data = data;
	});
	$("#stockContent").jqGrid({
		data: result_data,
		datatype: "local",
		mtype: 'POST',
		contentType: 'application/json',
	   	colNames:['code','name','date', 'strategy', 'hsl_c5', 'hsl_b5', 'hsl_r5', 'hsl_c10', 'hsl_b10', 'hsl_r10',
	   	       'hsl_c20', 'hsl_b20', 'hsl_r20','hsl_c30', 'hsl_b30', 'hsl_r30','hsl_c60', 'hsl_b60', 'hsl_r60',
	   	       'hsl_c120', 'hsl_b120', 'hsl_r120','hsl_c250', 'hsl_b250', 'hsl_r250', 
	   	       'p5_a', 'p10_a','p20_a', 'p30_a', 'avg_good',
	   	       'p5_g', 'p10_g','p20_g', 'p30_g', 'grad_up',
	   	       'priceArea', 'ltszArea','gapBetSZ', 'pe'],
	   	colModel:[
	   		{name:'code',index:'code', width:80, align: 'center'},
	   		{name:'name',index:'name', width:50, align: 'center'},
	   		{name:'date',index:'date', width:100, align: 'center', formatter: 'date', formatoptions: {srcformat:'u',newformat:'Y-m-d'}},
	   		{name:'strategy',index:'strategy', width:50, align: 'center'},
	   		
	   		{name:'hslCurrent5',index:'d5', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslBefore5',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslCmp5',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'hslCurrent10',index:'d20', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslBefore10',index:'d30', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslCmp10',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'hslCurrent20',index:'dnow', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslBefore20',index:'d5', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslCmp20',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'hslCurrent30',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslBefore30',index:'d20', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslCmp30',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'hslCurrent60',index:'d30', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslBefore60',index:'dnow', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslCmp60',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'hslCurrent120',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslBefore120',index:'d20', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslCmp120',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'hslCurrent250',index:'d30', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslBefore250',index:'dnow', width:50, align: 'right', formatter: 'currency'},
	   		{name:'hslCmp250',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'p5ToAvg',index:'d30', width:50, align: 'right', formatter: 'currency'},
	   		{name:'p10ToAvg',index:'dnow', width:50, align: 'right', formatter: 'currency'},
	   		{name:'p20ToAvg',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		{name:'p30ToAvg',index:'d30', width:50, align: 'right', formatter: 'currency'},
	   		{name:'avgPriceGood',index:'d10', width:50, align: 'right', formatter: 'currency'},

	   		{name:'pToD5Grad',index:'dnow', width:50, align: 'right', formatter: 'currency'},
	   		{name:'pToD10Grad',index:'dnow', width:50, align: 'right', formatter: 'currency'},
	   		{name:'pToD20Grad',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		{name:'pToD30Grad',index:'dnow', width:50, align: 'right', formatter: 'currency'},
	   		{name:'gradUp',index:'d10', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'priceArea',index:'ltszArea', width:50, align: 'right', formatter: 'currency'},
	   		{name:'ltszArea',index:'ltszArea', width:50, align: 'right', formatter: 'currency'},
	   		
	   		{name:'gapBetSZ',index:'gapBetSZ', width:50, align: 'right', formatter: 'currency'},
	   		{name:'pe',index:'pe', width:50, align: 'right', formatter: 'currency'},
	   		
	   	],
	   	rowNum:20,
	   	rowList:[10,20,30, 50],
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
        	/*for(var i=0; i<rowData.length;i++){
        		var v = rowData[i];
        		colorShow(i, v.ddx, 'ddx', 100);
        		colorShow(i, v.d5, 'd5', 100);
        		colorShow(i, v.d10, 'd10', 100);
        		colorShow(i, v.dnow, 'dnow', 100);
        	}*/
        }
	});
	$("#stockContent").jqGrid('navGrid','#pageCount',{edit:false,add:false,del:false,refresh:false});
	$("#stockContent").jqGrid('navButtonAdd', "#pageCount", {
	     caption: "", title: "Reload Grid", buttonicon: "ui-icon-refresh",
	     onClickButton: function () {
	     	postAjaxRequest("rs/analyzer", function(data){
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
			$("#stockContent").jqGrid('setCell', index+1, field, data, {'background':'url(\'resources/css/images/bg-rowGreen.gif\') repeat-x'});
		}
		else if(data > 0) {
			$("#stockContent").jqGrid('setCell', index+1, field, data, {'background':'url(\'resources/css/images/bg-rowRed.gif\') repeat-x'});
		}
		else {
			$("#stockContent").jqGrid('setCell', index+1, field, data, "");
		}
	}
	
});
