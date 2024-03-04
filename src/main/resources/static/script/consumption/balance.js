$(document).ready(function() {
	getBalance();
});

function getBalance() {
	$.ajax({
		url : "/user-consumption/balance"
			}).then(
					function(data) {
						$("#offerName").text(data.commercialName);
						var effectiveDate = data.effectiveDate != null ? data.effectiveDate:'';
						var expireDate = data.expireDate != null ? data.expireDate:'';
						var bytesConsumedAmt = data.bytesConsumedAmt != null ? data.bytesConsumedAmt: '0';
						var bytesUnusedAmt = data.bytesUnusedAmt != null ? data.bytesUnusedAmt: '0';
						var smsConsumedAmt = data.smsConsumedAmt != null ? data.smsConsumedAmt: '0';
						var smsUnusedAmt = data.smsUnusedAmt != null ? data.smsUnusedAmt: '0';
						var minutesConsumedAmt = data.minutesConsumedAmt != null ? data.minutesConsumedAmt: '0';
						var minutesUnusedAmt = data.minutesUnusedAmt != null ? data.minutesUnusedAmt: '0';
						var dataChartName = 'dataChart';
						var dataConsumption = [bytesConsumedAmt, bytesUnusedAmt];
						paintChart(dataChartName, dataConsumption, "MB");

						var smsChartName = 'smsChart';
						var smsConsumption = [smsConsumedAmt,  smsUnusedAmt];
						paintChart(smsChartName, smsConsumption, "SMS ");

						var voiceChartName = 'voiceChart';
						var voiceConsumption = [minutesConsumedAmt, minutesUnusedAmt];
						paintChart(voiceChartName, voiceConsumption, "MIN");
						
						
						$("#effectiveDate").text("Fecha Inicio: "+effectiveDate+ " " );
						$("#expireDate").text("Fecha Vigencia: "+expireDate+ " " );
						$("#dataConsumed").text(bytesConsumedAmt + " Consumidos" );
						$("#dataAvailable").text(bytesUnusedAmt + " Disponibles");
						
						$("#smsConsumed").text(smsConsumedAmt + " Consumidos" );
						$("#smsAvailable").text(smsUnusedAmt + " Disponibles");

						$("#voiceConsumed").text(minutesConsumedAmt + " Consumidos");
						$("#voiceAvailable").text(minutesUnusedAmt + " Disponibles");
						
					});
}

function paintChart(chartName, consumptionData, measureUnit) {
	
	Chart.Chart.pluginService.register({
	    beforeDraw: function(chart) {
	        const width = chart.chart.width;
	        const height = chart.chart.height;
	        const ctx = chart.chart.ctx;
	        ctx.restore();
	        const fontSize = (height / 114).toFixed(2);
	        ctx.font = fontSize + "em sans-serif";
	        ctx.textBaseline = 'middle';
	        var total = chart.data.datasets[0].data.reduce(function(previousValue, currentValue, currentIndex, array, measureUnit) {
	        		var result = previousValue + currentValue;
	                return  currentValue + "/" + result;
	        });
	        const text = total;
	        const textX = Math.round((width - ctx.measureText(text).width) / 2);
	        const textY = height / 1.3;
	        ctx.fillText(text, textX, textY);
	        ctx.save();
	    },
	});
	
	var ctx = document.getElementById(chartName).getContext('2d');
	var data = {
		labels : [measureUnit + " Consumidos", measureUnit + " Disponible", ],
		datasets : [ {
			data : consumptionData,
			backgroundColor : [ "#5e5e55", "#5cb85c", ],
			hoverBackgroundColor : [ "#5e5e55", "#02a677", ]
		} ]
	
	};

	var promisedDeliveryChart = new Chart(document.getElementById(chartName), {
		type : 'doughnut',
		data : data,
		options : {
			responsive : true,
			legend : {
				display : false
			},
			circumference : 1 * Math.PI,
			rotation : 1 * Math.PI,
			cutoutPercentage : 80
		}
	});

}

