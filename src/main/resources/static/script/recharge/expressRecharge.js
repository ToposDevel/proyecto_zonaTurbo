$(document).ready(function() {
	cleanErrorMessage();
	validateCellphoneNumber();
});

$(window).bind("pageshow", function() {
	cleanFormData();
});

function cleanFormData() {
	$("#cellphoneNumber").val("");
	$("#cellphoneNumberConfirmation").val("");
	$("#planSelectedId").val("");
}

function selectPlan(){
	var plan = $("#planSelectedId");
	var superOfferType =  $("#planSelectedId option:selected").text().trim();
	$("#planSelectedName").val(superOfferType);
}


function validateCellphoneNumber() {
	$("#cellphoneNumberConfirmation").change(function() {
		cleanErrorMessage();
		var cellphoneNumberConfirmation = $(this).val();
		var cellphoneNumber =  $("#cellphoneNumber").val();
		if(cellphoneNumber != ''){
			if(cellphoneNumberConfirmation == cellphoneNumber){
				$.ajax({
			        type: 'GET',
			        url: '/recharge/expressRecharge/plans/' + cellphoneNumberConfirmation,
			        success: function(data) {
			        	$('#planSelectedId').find('option').remove().end();
			        	var errorMessage = data.errorMessage;
			        	if(errorMessage != null && errorMessage != ''){
			        		$("#cellphoneNumberConfirmation").val("");
			        		showMainViewError(errorMessage);
			        	}
			        	if(data.planList != null && data.planList.length > 0){
			        		$.each(data.planList,function(i,obj)
				                    {
			        			if(obj != null){
			        				var planSelectOptions="<option value="+obj.id+" selected> "+obj.name+"</option>";
				                    $(planSelectOptions).appendTo('#planSelectedId');
			        			}
				                      
				                    });
			        		//$('#planSelectedId').find('option').get(0).remove();
			        	}
			        },
			        error: function (error) {
			        	showMainViewError('No se ha podido validar el número turbored, por favor intente nuevamente más tarde.');
			        }
			    });
			} else {
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

