package com.topostechnology.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.domain.Phone;
import com.topostechnology.domain.User;
import com.topostechnology.exception.TrException;
import com.topostechnology.model.BalanceModel;
import com.topostechnology.model.MonthModel;
import com.topostechnology.repository.UserRepository;
import com.topostechnology.service.FUConsumptionService;
import com.topostechnology.service.UserService;

import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping("/user-consumption")
public class FUConsumptionController {
	
	private static final Logger logger = LoggerFactory.getLogger(FUConsumptionController.class);
	
    @Resource
    private UserRepository userRepository;
	
	@Value("${consumption.detail.last.months.number}")
	private String lastMonthsNumber;
	
	
	@Autowired
	private FUConsumptionService consumptionService;
	
	@Autowired
	private UserService userService;
	
	
	  @RequestMapping("/view-detail")
	    public ModelAndView load(Model model, Principal principal) throws TrException {
		  ModelAndView modelAndView = new ModelAndView("consumption/detailView");
		  try {
			  Integer lastMonthsNumberInt = Integer.valueOf(lastMonthsNumber);
			  List<MonthModel> lastMonthsList = consumptionService.getLastMonths(lastMonthsNumberInt);
			  modelAndView.addObject("lastMonthsList", lastMonthsList);
		  } catch (Exception e){
			  logger.error(e.getMessage());
			  throw new TrException("La operación no pudo ser realizada, intente nuevamente más tarde" );
		  }
	        return modelAndView;
	    }
	  

	    @RequestMapping(path = "/download-detail/pdf/{monthYear}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	    public ResponseEntity<InputStreamResource> getDetailAsPDF(@PathVariable("monthYear") String monthYear, Principal principal)
	            throws IOException, JRException {
	    	User user = userService.findByUserName(principal.getName());
	        String fileName = "Detalle consumo-" + monthYear + ".pdf";
	        byte[] fileContent = consumptionService.generatePDF(user, monthYear);
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "inline; filename=" + fileName);

	        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
	                .body(new InputStreamResource(new ByteArrayInputStream(fileContent)));
	    }
	    
	    @RequestMapping( "/balance" )
	    public @ResponseBody BalanceModel getBalance( Model model, Principal principal ) {
	    	User user = userService.findByUserName(principal.getName());
	    	Phone phone = user.getPhones().get(0);
	    	BalanceModel balanceModel = consumptionService.getBalance(phone.getCellphoneNumber()); 
	        return balanceModel;
	    }
	    
	    
	    
}
