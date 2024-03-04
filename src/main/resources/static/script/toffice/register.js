$(document).ready(function() {
//	cleanForm();
	validateCellphoneNumber();
});

$(window).bind("pageshow", function() {
//	cleanForm();
});

function cleanForm(){
		$('#cellphoneNumber').val('');
		$('#fullName').val('');
		$('#imei').val('');
		$('#email').val('');
		$('#price').val('');
}

function validateCellphoneNumber() {
	$("#cellphoneNumberConfirmation")
			.change(
					function() {
						cleanErrorMessage();
						var cellphoneNumberConfirmation = $(this).val();
						var cellphoneNumber = $("#cellphoneNumber").val();
						if (cellphoneNumber != '') {
							if (cellphoneNumberConfirmation != cellphoneNumber) {
								$('#cellphoneNumberConfirmation').val('');
								showMainViewError("Los numéros de celular ingresados no coinciden.");
							}
						}
					});

}


function showMainViewError(errorMessage){
	$("#errorMessage").text(errorMessage);
	$("#errorMessage").show();
}

function cleanErrorMessage(errorMessage){
	$("#errorMessage").text('');
	$("#errorMessage").hide();
}
