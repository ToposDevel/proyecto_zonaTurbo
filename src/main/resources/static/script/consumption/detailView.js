$(document).ready(
		function() {
			$('#download_btn').on(
					'click',
					function() {
						var monthYear = $('#monthYear').val();
						if (monthYear == "" || monthYear == '') {
							alert("Es necesario seleccionar un mes");
						} else {
							window.open("/user-consumption/download-detail/pdf/"
									+ monthYear, '_blank');
						}
					});
		});