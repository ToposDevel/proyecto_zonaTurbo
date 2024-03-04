package com.topostechnology.service;

import com.topostechnology.constant.UserConstants;
import com.topostechnology.model.ChekBalanceModel;
import com.topostechnology.model.TurboOfficePaymentLinkModel;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class BalanceService {




    public BindingResult validateBalance(ChekBalanceModel chekBalanceModel, BindingResult bindingResult) {


        if (!GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER,
                chekBalanceModel.getMsisdn())) {
            bindingResult.rejectValue("msisdn", "msisdn.number.then");
        }
        return  bindingResult;
    }
}
