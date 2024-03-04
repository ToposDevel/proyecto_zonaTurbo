$(document).ready(function(){
	if ($('#imeiCheckbox').is(":checked"))
	{
		$('#imeiDiv').show();
    	$('#cellphoneNumberDiv').hide();
	} else {
		$('#cellphoneNumberDiv').show();
        $('#imeiDiv').hide();
	}
    $('#imeiCheckbox').change(function(){
        if(this.checked){
            $('#imeiDiv').show();
        	$('#cellphoneNumberDiv').hide();
        }else{
            $('#cellphoneNumberDiv').show();
            $('#imeiDiv').hide();
        }
    });
});


