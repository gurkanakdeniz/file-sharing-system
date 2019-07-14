/**
 * TODO:GA: duzenle
 * */
//(function() {
//	'use strict';
//	window.addEventListener('load', function() {
//		// Fetch all the forms we want to apply custom Bootstrap validation styles to
//		var forms = document.getElementsByClassName('needs-validation');
//		// Loop over them and prevent submission
//		var validation = Array.prototype.filter.call(forms, function(form) {
//			form.addEventListener('submit', function(event) {
//				if (form.checkValidity() === false) {
//					event.preventDefault();
//					event.stopPropagation();
//				}
//				form.classList.add('was-validated');
//			}, false);
//		});
//	}, false);
//})();

(function() {
	'use strict';
	window.addEventListener('load', function() {
		var forms = document.getElementsByClassName('needs-validation');
		var validation = Array.prototype.filter.call(forms, function(form) {
			form.addEventListener('submit', function(event) {
				if (form.checkValidity() === false) {
					event.preventDefault();
					event.stopPropagation();
				}
				form.classList.add('was-validated');
			}, false);
		});
	}, false);
})();
$(document).on('blur', '[data-validator]', function () {
    new Validator($(this));
});

function toggleLogin() {
	$("#register").toggleClass("hidden");
	$("#login").toggleClass("hidden");
}


$(document).ready(function() {


});
