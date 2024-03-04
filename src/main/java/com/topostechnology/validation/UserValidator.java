package com.topostechnology.validation;

import org.springframework.validation.BindingResult;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.model.UserModel;
import com.topostechnology.utils.StringUtils;

public class UserValidator {
	
	public static BindingResult validate(UserModel userModel, BindingResult bindingResult) {
		if(!userModel.isLoginWithImei() ) {
			if(!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER, userModel.getCellphoneNumber())) {
				bindingResult.rejectValue("cellphoneNumber", "cellphone.number.then");
			}
		} else {
			if(!GeneralValidator.validatePattern(UserConstants.REGEX_IMEI_NUMBER, userModel.getImei())) {
				bindingResult.rejectValue("imei", "imei.number.fifteen");
			}
		}
		if (StringUtils.isBlank(userModel.getPassword()) || StringUtils.isBlank(userModel.getPasswordConfirmation())) {
            bindingResult.rejectValue("password", "password.not.empty.message");
        }
        if (!userModel.getPassword().equals(userModel.getPasswordConfirmation())) {
            bindingResult.rejectValue("password", "password.not.equal.message");
        }
		return bindingResult;
	}
	
}
