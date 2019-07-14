function renderFileProcess(data) {
	if (data) {
		return '<a href="user/file/download/'+ data.userId + '/' + data.fileId +'" class="btn btn-success margin-dot-two" type="button" name="button">D</a>'
		+ '<a href="user/file/remove/'+ data.userId + '/' + data.fileId +'"  class="btn btn-warning margin-dot-two" type="button" name="button">R</a>'
		+ '<a href="user/file/delete/'+ data.userId + '/' + data.fileId +'"  class="btn btn-danger margin-dot-two" type="button" name="button">D</a>';
	}
	
	return "";
}

function newUserFileListTable(tableId){
	$('#' + tableId).DataTable({
		"data" : [],
		"columns" : [ {
			"data" : "name"
		}, {
			"data" : "date"
		}, {
			"data" : "size"
		}, {
			"data" : "process",
			"render" : function(data, type, row) {
				return renderFileProcess(data);
			}
		} ],
		"columnDefs" : [ {
			className : "user-file-list",
			targets : "_all"
		} //"targets": [ 0,1,2,3 ] }
		],
		retrieve : true
	// tablo bir kere olusturulduktan sonra tekrar olusturmak icin
	});	
}

function reloadUserFileListTableData(data, tableId){
	var datatable = $('#' + tableId).DataTable();
    datatable.clear();
    datatable.rows.add(data);
    datatable.draw();
}


function getUserFiles(url, userId) {
	var tableId = 'user-file-list';
	newUserFileListTable(tableId);
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : url,
		data : userId,
		dataType : 'json',
		cache : false,
		success : function(result) {
			
			try {
				reloadUserFileListTableData([], tableId);
			} catch (err) {
			}
			
			try {
				reloadUserFileListTableData(result, tableId);
			}catch (err){
				reloadUserFileListTableData([], tableId);
			}
			
		},
        error: function(jqXHR, textStatus, errorThrown) {
        	reloadUserFileListTableData([], tableId);
        },
	});
}

$(document).ready(function() {
	try {
		$('#user-list-table').DataTable();
	} catch (err) {
		//
	}
});