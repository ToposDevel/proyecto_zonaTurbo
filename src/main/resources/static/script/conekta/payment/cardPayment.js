$(document).ready(function() {
	cleanErrorMessage();
	cleanCardForm();
});

$(window).bind("pageshow", function() {
	cleanCardForm();
});

function cleanCardForm() {
	$('#cardTitName').val('');
	$('#cardNumber').val('');
	$('#cardExpiredMonth').val('');
	$('#cardExpiredYear').val('');
	$('#cardCvv').val('');
}

function createToken() {
	var valid = validateCardForm();
	if (valid) {
	$('body').addClass("loading");
		$
				.ajax({
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
							$("#card-form").submit();
						};

						var errorResponseHandler = function(error) {
							showError(error.message_to_purchaser);
							$('body').removeClass("loading");
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
						Conekta.Token.create(tokenParams,
								successResponseHandler, errorResponseHandler);

					},
					error : function(error) {
						showMainViewError('No se ha podido procesar el pago, por favor intente nuevamente más tarde.');
					}
				});
	}
}

function validateCardForm() {
	return true;
}

function showError(errorMessage) {
	$("#errorMessage").text(errorMessage);
	$("#errorMessage").show();
}

function cleanErrorMessage(errorMessage) {
	$("#errorMessage").text('');
	$("#errorMessage").hide();
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
					showMainViewError('No se ha completar procesar el pago, por favor intente nuevamente más tarde.');
				}
			});
	return conektaPublicToken;
}
