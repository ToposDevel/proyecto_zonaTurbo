$(document).ready(function() {

	$("#myTableSearch").on("keyup", function() {
		var value = $(this).val().toLowerCase();
		$("#callDetails tr").filter(function() {
			$(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
		});
	});

	$('#callDetailsDiv').hide();
	$('#callDetailButton').on('click', function() {
		getCallDetails();
	});
	$('#myDeleteButton').hide();
	$('#myUnlockButton').hide();
	$('#codeDiv').hide();
	$('#validateIdentityCodeBtn').hide();
	
	

	

});

function deleteUser() {
	var virtualNumber = $('#virtual_number').val();
	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOfficeUser/delete/' + virtualNumber,
				success : function(data) {
//					alert(data);
					var url = "../adminTurboOffice";
			        $(location).attr('href',url);
				},
				error : function(error) {
					showMainViewError('No se ha podido eliminar el usuario, por favor intente nuevamente más tarde.');
				}
			});
}

function getPaymentLink(){
	var virtualNumber = $('#virtual_number').val();
	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOfficeUser/generatePaymentLink/' + virtualNumber,
				success : function(data) {
					//alert(data);
					window.open(data, '_blank').focus();
				},
				error : function(error) {
					showMainViewError('No se ha podido generar el link de pago, por favor intente nuevamente más tarde.');
				}
			});

}

function unlockAppUser(){
	var virtualNumber = $('#virtual_number').val();
	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOffice/unlockAcrobitsUser/' + virtualNumber,
				success : function(data) {
					 //alert(data);
					 window.location.reload();
				},
				error : function(error) {
					showMainViewError('No se ha podido desbloquear el usuario, por favor intente nuevamente más tarde.');
				}
			});
}

function getCallDetails(){
	 $('#callDetailsDiv').show();
	var turboOfficeUserPlanDetailId = $('#turboOfficeUserPlanDetailId').val();
	if (turboOfficeUserPlanDetailId == "" || turboOfficeUserPlanDetailId == '') {
		alert("Es necesario seleccionar un periodo");
	} else {
        var dataTable = $('#callDetails').DataTable({
        	 paging: false,
             ordering: false,
             info: false,
             searching: false,
             serverSide: true,
             processing: true,
             destroy: true,
            ajax: {
                "url": "/adminTurboOfficeUser/getCallsDetail/"+turboOfficeUserPlanDetailId,
                "data": function (data) {
                	
                }
            },
            columns: [{
                "data": "destination"
            }, {
                "data": "minutes"
            }, {
                "data": "startAt"
            }, {
                "data": "stopAt"
            }]
        });

	}
}

function sendValidationCode(){
	var virtualNumber = $('#virtual_number').val();
	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOffice/sendValidationCode/' + virtualNumber,
				success : function(data) {
//					 alert(data);
					 if(data == true){
						 acrobitsLockedStatus = $('#acrobitsLockedStatus').val();
						 $('#codeDiv').show();
						 $('#validateIdentityBtn').hide();
						 $('#validateIdentityCodeBtn').show();
						 showConfirmationMessage('Codigo enviado');
					 } else {
						 showMainViewError('Código no pudo ser enviado.');
					 }
					 
				},
				error : function(error) {
					showMainViewError('No se ha podido enviar el codigo para validar al usuario, por favor intente nuevamente más tarde.');
				}
			});
}


function validateIdentityCode(){
	var virtualNumber = $('#virtual_number').val();
	var validationCode = $('#validationCode').val();
	var validationCodeConfirmation = $('#validationCodeConfirmation').val();
	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOffice/validateCode/' + virtualNumber + '/' + validationCode ,
				success : function(data) {
//					 alert(data);
					 if(data == true){
						 $('#myDeleteButton').show();
						 cleanErrorMessage();
						 showConfirmationMessage('Identidad validada');
						 $('#validationCode').val('');
						 $('#codeDiv').hide();
						 $('#validateIdentityCodeBtn').hide();
						 $('#identityBtn').hide();
						 acrobitsLockedStatus = $('#acrobitsLockedStatus').val();
						 if (acrobitsLockedStatus == 'true'){
							 $('#myUnlockButton').show();
						 }
					 } else {
						 showMainViewError('Código invalido.');
						 $('#validationCode').val('');
					 }
				},
				error : function(error) {
					showMainViewError('No se ha podido validar el codigo para validar al usuario, por favor intente nuevamente más tarde.');
				}
			});
}

function showMainViewError(errorMessage) {
	cleanConfirmationMessage();
	$("#errorMessage").text(errorMessage);
	$("#errorMessage").show();
}

function cleanErrorMessage() {
	$("#errorMessage").text('');
	$("#errorMessage").hide();
}

function showConfirmationMessage(errorMessage) {
	cleanErrorMessage();
	$("#confirmationMessage").text(errorMessage);
	$("#confirmationMessage").show();
}

function cleanConfirmationMessage() {
	$("#confirmationMessage").text('');
	$("#confirmationMessage").hide();
}