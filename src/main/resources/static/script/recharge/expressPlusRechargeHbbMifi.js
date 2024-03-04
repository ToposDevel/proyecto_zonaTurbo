$(document).ready(function() {
	cleanErrorMessage();
	validateCellphoneNumber();
});

$(window).bind("pageshow", function() {
	cleanFormData();
});

function cleanFormData() {
	$("#imei").val("");
	$("#email").val("");
	$("#imeiConfirmation").val("");
	$("#planSelectedId").val("");
	$("#email").val("");
}

function selectPlan(){
	cleanErrorMessage();
	var planSelected = $("#planSelected").val();
	var plaSelectedData = planSelected.split("-");
	var offerTypeName =  $("#planSelected option:selected").text().trim();
	$("#planSelectedName").val(offerTypeName);
	$("#planSelectedId").val(plaSelectedData[0]);
	$("#amount").val(plaSelectedData[1]);
}


function validateCellphoneNumber() {
	$("#imeiConfirmation").change(function() {
		cleanErrorMessage();
		var imeiConfirmation = $(this).val();
		var imei =  $("#imei").val();
		if(imei != ''){
			if(imeiConfirmation == imei){
				$.ajax({
			        type: 'GET',
			        url: '/recharge/expressPlusRecharge/plans/' + imeiConfirmation,
			        success: function(data) {
			        	var imeiConfirmationSize = imeiConfirmation.length;
			        	if(imeiConfirmationSize == 15){
			        		$("#cellphoneNumber").val(data.cellphoneNumber);
			        		$("#cellphoneNumberConfirmation").val(data.cellphoneNumber);
			        		$('#imeicellphoneNumber').text(data.cellphoneNumber);
			        		$('#associatedNumberDiv').show();
			        	}else{
			        		$("#cellphoneNumber").val(imeiConfirmation);
			        		$("#cellphoneNumberConfirmation").val(imeiConfirmation);
			        	}
			        	
			        	$('#planSelected').find('option').remove().end();
			        	var errorMessage = data.errorMessage;
			        	if(errorMessage != null && errorMessage != ''){
			        		$("#imeiConfirmation").val("");
			        		showMainViewError(errorMessage);
			        	}
			        	if(data.planList != null && data.planList.length > 0){
			        		$.each(data.planList,function(i,obj)
				                    {
			        			if(obj != null){
			        				var planSelectOptions="<option value="+obj.id+'-'+obj.amount+" selected> "+obj.name+"</option>";
				                    $(planSelectOptions).appendTo('#planSelected');
			        			}
			        			
				                      
				                    });
			        		selectPlan();
			        		//$('#planSelectedId').find('option').get(0).remove();
			        	}
			        },
			        error: function (error) {
			        	showMainViewError('No se ha podido validar el número turbored, por favor intente nuevamente más tarde.');
			        }
			    });
			} else {
				showMainViewError("Los numéros ingresados no coinciden.");
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

