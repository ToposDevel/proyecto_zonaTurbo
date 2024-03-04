$(document).ready(function() {
	cleanErrorMessage();
	cleanCardViewErrorMessage();
	cleanCardForm();
	$("#cardDataModal").on('hidden.bs.modal', function() {
		cleanCardForm();
	});
	validateCellphoneNumber();
	validateImei();
	showMovOrHbbMifiForm();
});

$(window).bind("pageshow", function() {
	cleanCardForm();
	cleanMainForm();
});

function showMovOrHbbMifiForm() {
	var suscriptionWithImei = $('#suscriptionWithImei').val();
	if (suscriptionWithImei == 'true') {
		$('#cellphoneNumberDiv').hide();
		$('#imeiDiv').show();
	} else {
		$("#imeiDiv").hide();
		$("#cellphoneNumberDiv").show();
	}
}

function validateImei() {
	cleanErrorMessage();
	var suscriptionWithImei = $('#suscriptionWithImei').val();
	$("#imeiConfirmation").change(function() {
		var imei = $(this).val();
		if (suscriptionWithImei == 'true') {
			getOffersHbbAndMifi(imei);
		}
	});
}

function validateCellphoneNumber() {
	cleanErrorMessage();
	$("#cellphoneNumberConfirmation")
			.change(
					function() {
						var cellphoneNumberConfirmation = $(this).val();
						var cellphoneNumber = $("#cellphoneNumber").val();
						if (celphoneNumber != '') {
							if (cellphoneNumberConfirmation == cellphoneNumber) {
								var celphoneNumber = $(this).val();
								getOffersMov(celphoneNumber);
							} else {
								showMainViewError("Los numéros de celular ingresados no coinciden.");
							}
						}
					});
}

function getOffersMov(cellphoneNumber) {
	var url = '/conekta/subscription/offersMov/' + cellphoneNumber;
	getPlans(url);
}

function getOffersHbbAndMifi(imei) {
	var url = '/conekta/subscription/offersHbbMifi/' + imei;
	getPlans(url);
}

function getPlans(url) {
	$
			.ajax({
				type : 'GET',
				url : url,
				success : function(data) {
					cleanErrorMessage();
					$('#superOfferType').val(data.superOfferType);
					$('#imeicellphoneNumber').text(data.cellphoneNumber);
					$('#planSelectedId').find('option').remove().end();
					//$("#internalOperation").val(data.internalOperation)
					$("#internalOperation").val(data.altanOperation)
					var errorMessage = data.errorMessage;
					if (errorMessage != null && errorMessage != '') {
						$("#cellphoneNumberConfirmation").val("");
						showMainViewError(errorMessage);
					}
					var cellphoneNumber = $('#cellphoneNumber').val();
					if (cellphoneNumber == '') {
						$('#cellphoneNumber').val(data.cellphoneNumber);
						$('#cellphoneNumberConfirmation').val(
								data.cellphoneNumber);
					}
					if (data.planList != null && data.planList.length > 0) {
						$.each(data.planList, function(i, obj) {
							if (obj != null) {
								var planSelectOptions = "<option value="
										+ obj.id + " selected> " + obj.name
										+ "</option>";
								$(planSelectOptions)
										.appendTo('#planSelectedId');
							}

						});
						validateSuperOfferType();
						//$('#planSelectedId').find('option').get(0).remove();
					}
				},
				error : function(error) {
					showMainViewError('No se ha podido validar el número turbored, por favor intente nuevamente más tarde.');
				}
			});
}


function getConektaPublicToken() {
	var conektaPublicToken = null;
	$
			.ajax({
				type : 'GET',
				url : '/conekta/getConektaPublicToken',
				success : function(data) {
					conektaPublicToken = data;
					alert('conektaPublicToken: ' + conektaPublicToken);

				},
				error : function(error) {
					showMainViewError('No se ha completar la domiciliacion, por favor intente nuevamente más tarde.');
				}
			});
	return conektaPublicToken;
}

function validateCardForm() {
	cleanCardViewErrorMessage();
	var mail = $('#e-mail').val();
	var testEmail = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
	if (testEmail.test(mail)) {
		return true;
	} else {
		showCardViewErrorMessage("Correo electrónico invalido.");
		return false;
	}
}

function cleanMainForm() {
	$('#cellphoneNumber').val('');
	$('#cellphoneNumberConfirmation').val('');
	$('#termsAndConditionsChbx').prop('checked', false)
	$('#planSelectedId').find('option').remove().end();
	$("#imei").val('');
	$("#imeiConfirmation").val('');
}

function cleanCardForm() {
	$('#cardTitName').val('');
	$('#cardNumber').val('');
	$('#cardExpiredMonth').val('');
	$('#cardExpiredYear').val('');
	$('#cardCvv').val('');
	$('#e-mail').val('');
}

function goToCardForm() {
	$('#cardDataModal').modal({
		backdrop : 'static',
		keyboard : false,
		show : true
	})
}

function createToken() {
	var valid = validateCardForm();
	if (valid) {
		$.ajax({
			type : 'GET',
			url : '/conekta/getConektaPublicToken',
			success : function(data) {
				var publicConektaApiKey = data;
				Conekta.setPublicKey(publicConektaApiKey);
				Conekta.getPublicKey();
				Conekta.setLanguage("es");

				var successResponseHandler = function(token) {
					$('#tokenId').val(token.id);
					$('#cardTitularName').val($('#cardTitName').val());
					$('#email').val($('#e-mail').val());
					$("#card-form").submit();
				};

				var errorResponseHandler = function(error) {
					$('#errorMessageCard').text(error.message_to_purchaser);
					$("#errorMessageCard").show();
				};

				var cardTitularName = $('#cardTitName').val();
				var cardNumber = $('#cardNumber').val();
				var cardExpiredMonth = $('#cardExpiredMonth').val();
				var cardExpiredYear = $('#cardExpiredYear').val();
				var cardCvv = $('#cardCvv').val();

				var tokenParams = {
					"card" : {
						"number" : cardNumber,
						"name" : cardTitularName,
						"exp_year" : cardExpiredYear,
						"exp_month" : cardExpiredMonth,
						"cvc" : cardCvv,
						"address" : {
							"street1" : "",
							"street2" : "",
							"city" : "",
							"state" : "",
							"zip" : "",
							"country" : ""
						}
					}
				};
				Conekta.Token.create(tokenParams, successResponseHandler,
						errorResponseHandler);

			},
			error : function(error) {
				showMainViewError('No se ha completar la domiciliacion, por favor intente nuevamente más tarde.');
			}
		});
		
		
	}
}

function validateMainDataForm() {
	var cellphoneNumber = $("#cellphoneNumber").val();
	var cellphoneNumberConfirmation = $("#cellphoneNumber").val();
	var cellphoneNumber = $("#cellphoneNumber").val();
	var cellphoneNumberConfirmation = $("#cellphoneNumberConfirmation").val();
	var planSelectedId = $("#planSelectedId").val();
	var termsAndConditionsChbx = $("#termsAndConditionsChbx").prop('checked');
	if (cellphoneNumber == '' || cellphoneNumberConfirmation == ''
			|| planSelectedId == null || planSelectedId == ''
			|| !termsAndConditionsChbx) {
		showMainViewError('Todos los campos son obligatorios.');
	} else {
		var suscriptionWithImei = $('#suscriptionWithImei').val();
		if (suscriptionWithImei == 'true') {
			var imei = $("#imei").val();
			var imeiConfirmation = $("#imeiConfirmation").val();
			if (imei != imeiConfirmation) {
				showMainViewError('Los números de IMEI ingresados no coinciden.');
			} else {
				cleanErrorMessage();
				goToCardForm();
			}
		} else {
			if (cellphoneNumber != cellphoneNumberConfirmation) {
				showMainViewError('Los números de celular ingresados no coinciden.');
			} else {
				cleanErrorMessage();
				goToCardForm();
			}
		}
	}
}

function showMainViewError(errorMessage) {
	$("#errorMessage").text(errorMessage);
	$("#errorMessage").show();
}

function cleanErrorMessage(errorMessage) {
	$("#errorMessage").text('');
	$("#errorMessage").hide();
}

function showCardViewErrorMessage(errorMessage) {
	$('#errorMessageCard').text(errorMessage);
	$("#errorMessageCard").show();
}

function cleanCardViewErrorMessage(errorMessage) {
	$("#errorMessageCard").text('');
	$("#errorMessageCard").hide();
}

function validateSuperOfferType() {
	var plan = $("#planSelectedId");
	var planSelectedId = $("#planSelectedId").val();
	var superOfferType = $("#planSelectedId option:selected").text().trim();

	var initStr = superOfferType.substring(0, 3);
	if (initStr == 'HBB') {
		$("#coordinatesDiv").show();
		$("#belongsToHhb").val(true);
	}
}

function validateCoordinates(coordinates) {
	cleanErrorMessage();
	var valid = false;
	$
			.ajax({
				type : 'GET',
				url : '/conekta/subscription/validateCoverage/' + coordinates,
				success : function(data) {
					if (data == true) {
						goToCardForm();
					} else {
						showMainViewError('El servicio de altan no esta disponible en las coordenadas ingresadas.');
					}
				},
				error : function(error) {
					showMainViewError("Las coordenadas no pudieron ser validadas, verifique e intente nuevamente.");
				}
			});
	return valid;
}
