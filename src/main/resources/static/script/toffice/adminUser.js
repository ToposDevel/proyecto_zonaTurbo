$(".myDeleteButton").click(function(e) {

   	  var num = $(this);
      var numVirtual = num.data("numvirtual");
     // alert(numVirtual);
      $("#virtualNumberSelected").val(numVirtual);

});

$(".desvincularButton").click(function(e) {

   	  var num = $(this);
      var numVirtual = num.data("numvirtual");
      var numAssociated = num.data("numassociate");
     // alert(numVirtual);
      $("#virtualNumberSelected").val(numVirtual);
      $("#associatedNumberSelected").val(numAssociated);

});
$(".resetearButton").click(function(e) {

   	  var num = $(this);
      var numVirtual = num.data("numvirtual");

     // alert(numVirtual);
      $("#virtualNumberSelected").val(numVirtual);

});

function deleteUser() {

   var virtualNumber =   $("#virtualNumberSelected").val();

   //alert(numVirtual)


	var url = "../adminTurboOffice/adminUsers";



	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOfficeUser/delete/' + virtualNumber,
				success : function(data) {
//					alert(data);
					window.location.reload();
				},
				error : function(error) {
					showMainViewError('No se ha podido eliminar el usuario, por favor intente nuevamente más tarde.');
				}
			});
}

function unlockAppUser(){
	 var virtualNumber =   $("#virtualNumberSelected").val();
	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOffice/unlockAcrobitsUser/' + virtualNumber,
				success : function(data) {
					 //alert(data);
					 window.location.reload();
				},
				error : function(error) {
					showMainViewError('No se ha podido resetear el password del usuario, por favor intente nuevamente más tarde.');
				}
			});
}

function desvincularUser(){
	 var virtualNumber =   $("#virtualNumberSelected").val();
	 var associatedNumber = $("#associatedNumberSelected").val();
	$
			.ajax({
				type : 'GET',
				url : '/adminTurboOffice/unlinkVirtualNumber/' + virtualNumber+'/'+associatedNumber,
				success : function(data) {
					 //alert(data);
					 window.location.reload();
				},
				error : function(error) {
					showMainViewError('No se ha podido desvincular el numero, por favor intente nuevamente más tarde.');
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