$(document).ready(function(){
	if ($('#imeiCheckbox').is(":checked"))
	{
		$('#username').attr('placeholder','IMEI a 15 dígitos ej. 123456106543211');
    	$('#username').attr('maxlength', 15);
    	$('#username').attr('minlength', 15);
    	$('#usernameLbl').text('IMEI');
	} else{
		$('#username').attr('placeholder','Número celular a 10 dígitos ej. 5586410100');
    	$('#username').attr('maxlength', 10);
    	$('#username').attr('minlength', 10);
    	$('#usernameLbl').text('Teléfono');
	}
    $('#imeiCheckbox').change(function(){
        if(this.checked){
        	$('#username').attr('placeholder','IMEI a 15 dígitos ej. 123456106543211');
        	$('#username').attr('maxlength', 15);
        	$('#username').attr('minlength', 15);
        	$('#usernameLbl').text('IMEI');
        } else{
        	$('#username').attr('placeholder','Número celular a 10 dígitos ej. 5586410100');
        	$('#username').attr('maxlength', 10);
        	$('#username').attr('minlength', 10);
        	$('#usernameLbl').text('Teléfono');
        }
    });
});


