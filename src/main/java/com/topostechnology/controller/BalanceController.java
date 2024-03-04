package com.topostechnology.controller;

import com.topostechnology.exception.TrException;
import com.topostechnology.model.BalanceModel;
import com.topostechnology.model.ChekBalanceModel;
import com.topostechnology.model.CompatibilityModel;
import com.topostechnology.model.PortingModel;
import com.topostechnology.rest.client.response.ProfileDataResponse;
import com.topostechnology.service.BalanceService;
import com.topostechnology.service.FUConsumptionService;
import com.topostechnology.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;



@Controller
public class BalanceController {

    @Autowired
    private BalanceService balanceService;
    @Autowired
    private FUConsumptionService consumptionService;
    @Autowired
    private ProfileService profileService;


    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);
    @RequestMapping(value = "/checkbalance/", method = RequestMethod.POST)
    public ModelAndView porting(ModelAndView modelAndView,
                                @Valid @ModelAttribute("checkbalanceForm") final ChekBalanceModel chekBalanceModel,
                                BindingResult bindingResult, HttpServletRequest request, Errors errors) {
        logger.info("Empezando a revisar el número " + chekBalanceModel.getMsisdn());
        modelAndView = new ModelAndView("balance/checkbalance");
        bindingResult = balanceService.validateBalance(chekBalanceModel, bindingResult);

        if (!bindingResult.hasFieldErrors()) {
            try {

                ProfileDataResponse profile = profileService.getProfile(chekBalanceModel.getMsisdn());
                Boolean isTurboredMsisdn = profile != null;

                if (isTurboredMsisdn) {

                    BalanceModel balanceModel = consumptionService.getBalance(chekBalanceModel.getMsisdn());
                    String bytesConsumedAmt = balanceModel.getBytesConsumedAmt() != null ? balanceModel.getBytesConsumedAmt().toString() : "0";
                    String bytesUnusedAmt = balanceModel.getBytesUnusedAmt() != null ? balanceModel.getBytesUnusedAmt().toString() : "0";


                    String smsConsumedAmt = balanceModel.getSmsConsumedAmt() != null ? balanceModel.getSmsConsumedAmt().toString() : "0";
                    String smsUnusedAmt = balanceModel.getSmsUnusedAmt() != null ? balanceModel.getSmsUnusedAmt().toString() : "0";

                    String minutesConsumedAmt = balanceModel.getMinutesConsumedAmt() != null ? balanceModel.getMinutesConsumedAmt().toString() : "0";
                    String minutesUnusedAmt = balanceModel.getMinutesUnusedAmt() != null ? balanceModel.getMinutesUnusedAmt().toString() : "0";

                    String datos = bytesConsumedAmt + " MB consumidos, " + bytesUnusedAmt + " MB disponibles";
                    String mensajes = smsConsumedAmt + " Mensajes consumidos, " + smsUnusedAmt + " Mensajes disponibles";
                    String minutos = minutesConsumedAmt + " Minutos consumidos, " + minutesUnusedAmt + " Minutos disponibles";
                    modelAndView.addObject("confirmationDatos", datos);
                    modelAndView.addObject("confirmationMensajes", mensajes);
                    modelAndView.addObject("confirmationMinutos", minutos);
                }else{
                    modelAndView.addObject("failedMessage", ProfileService.CELPHONE_VALIDATION_PROBLEM + " "+ ProfileService.NO_TURBORED);
                }

            } catch (Exception e) {
                logger.error("Error on porting.", e.getMessage());
                bindingResult.reject("failedMessage",
                        "La operación no pudo ser realizada, intente nuevamente más tarde");
            }
        }
        return modelAndView;
    }


    @RequestMapping("/checkbalance/")
    public ModelAndView load(Model model, Principal principal) {
        logger.info("Entrando a vista de saldo");
        ChekBalanceModel chekBalanceModel = new ChekBalanceModel();
        ModelAndView modelAndView = new ModelAndView("balance/checkbalance");
        modelAndView.addObject("checkbalanceForm", chekBalanceModel);
        return modelAndView;
    }
}