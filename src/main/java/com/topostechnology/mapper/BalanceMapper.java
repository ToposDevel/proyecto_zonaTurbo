package com.topostechnology.mapper;

import java.text.DecimalFormat;

import com.topostechnology.model.BalanceModel;
import com.topostechnology.rest.client.response.FreeUnitsSelf;
import com.topostechnology.rest.client.response.Minutes;
import com.topostechnology.rest.client.response.SelfConsumption;
import com.topostechnology.rest.client.response.Sms;
import com.topostechnology.utils.DateUtils;

public class BalanceMapper {

	public static BalanceModel convertSelfConsumptionToBalanceModel(SelfConsumption selfConsumption) {
		BalanceModel balanceModel = null;
		if (selfConsumption != null) {
			balanceModel = new BalanceModel();
			FreeUnitsSelf freeUnitsSelf = selfConsumption.getFreeUnits();
			Sms sms = selfConsumption.getSms();
			Minutes minutes = selfConsumption.getMinutes();
			if (freeUnitsSelf != null) {
				balanceModel.setBytesTotalAmt(freeUnitsSelf.getTotalAmt());
				balanceModel.setBytesConsumedAmt(format(freeUnitsSelf.getConsumedAmt()));
				balanceModel.setBytesUnusedAmt(format(freeUnitsSelf.getUnusedAmt()));
				balanceModel.setEffectiveDate(DateUtils.formatYYMMDD(freeUnitsSelf.getEffectiveDate()));
				balanceModel.setExpireDate(DateUtils.formatYYMMDD(freeUnitsSelf.getExpireDate()));
			}
			if (sms != null) {
				balanceModel.setSmsTotalAmt(sms.getTotalAmt());
				balanceModel.setSmsConsumedAmt(sms.getConsumedAmt());
				balanceModel.setSmsUnusedAmt(sms.getUnusedAmt());
			}
			if (minutes != null) {
				balanceModel.setMinutesTotalAmt(minutes.getTotalAmt());
				balanceModel.setMinutesConsumedAmt(minutes.getConsumedAmt());
				balanceModel.setMinutesUnusedAmt(minutes.getUnusedAmt());
			}
		}
		return balanceModel;
	}
	
	public static Double decimalFormat(Double number) {
		Double value = Math.floor(number * 100) / 100;
		return value;
	}
	
	private static Double format(Double number) {
		Double twoDecimal = number;
		if(number != null) {
			DecimalFormat newFormat = new DecimalFormat("#.##");
			twoDecimal =  Double.valueOf(newFormat.format(number/1.00));
		}
		return twoDecimal;
	}
}
