//window.onbeforeunload = function(e) {
//	console.log('cerrando');
//	$.ajax({
//		url : '/logout',
//		type : 'GET',
//		success : function(data) {
//			console.log('logout success');
//		},
//		error : function(data) {
//			console.log(data);
//		}
//	});
//
//};


// Carga ventana modal cuando se hace peticion ajax
$body = $("body");

$(document).on({
    ajaxStart: function() { $body.addClass("loading");    },
     ajaxStop: function() { $body.removeClass("loading"); }   
    
    
    
    
});

//$("form").on('submit', function(){
//	   $('.modal').show();
//})

$("form").on('submit', function(){
	$body.addClass("loading");
})

