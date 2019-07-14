function changeName(index) {
	$("#newName" + index).toggleClass("hidden");
}

function submitName(oldFileName, index) {
	var newFileName = $("#newFileName" + index).val();

	if (newFileName) {
		location.href = location.href + "/changeName/?fileName=" + oldFileName
				+ "&newFileName=" + newFileName;
	}
}

$(document).ready(function() {

	try {
		$('#files').DataTable();
	} catch (err) {
		//
	}

});