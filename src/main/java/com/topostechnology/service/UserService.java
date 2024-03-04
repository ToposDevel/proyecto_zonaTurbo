package com.topostechnology.service;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.topostechnology.constant.RoleConstants;
import com.topostechnology.constant.UserConstants;
import com.topostechnology.domain.ConfirmationToken;
import com.topostechnology.domain.Phone;
import com.topostechnology.domain.Role;
import com.topostechnology.domain.User;
import com.topostechnology.exception.TrException;
import com.topostechnology.mapper.UserMapper;
import com.topostechnology.model.UserModel;
import com.topostechnology.repository.BaseRepository;
import com.topostechnology.repository.ConfirmationTokenRepository;
import com.topostechnology.repository.PhoneRepository;
import com.topostechnology.repository.UserRepository;
import com.topostechnology.rest.client.AcrobitUserClient;
import com.topostechnology.rest.client.GategayApiClient;
import com.topostechnology.rest.client.request.AcrobitsUserRequest;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.GeneralValidator;

@Service
public class UserService extends CoreCatalogService<User> {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;
    
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    
    @Resource
    private EmailService emailService;
    
    @Value("${turbo.consumo.web.url}")
    private String turboConsumptionWebUrl;
    
    @Value("${acrobits.cloudid}")
    private String cloudId;
    
    private static final String CONFIRM_RESET_PASSWORD = "users/confirm-reset";
    
	@Autowired
	private SpringTemplateEngine templateEngine;
	

    @Autowired
    public UserService(UserRepository userRepository) {
        super("User");
        this.userRepository = userRepository;
    }
    
    @Autowired
    private PasswordEncoder myPasswordEncoder;
    
	@Autowired
	private FUConsumptionService consumptionDetailService;
	
	@Resource
	private PhoneRepository phoneRepository;
	
	@Autowired
	private GategayApiClient gategayApiClient;
	
	@Autowired
	private AcrobitUserClient acrobitUserClient;
	
	
	@Value("${acrobits.activate}")
	private boolean activateAcrobits;

	public User findByUserName(String userName) {
		User user = userRepository.findByUserName(userName);
		return user;
	}
	
	@Transactional
    public User create(UserModel userModel) throws Exception {
		boolean registeredInacrobits = false;
		String msisdn = null;
		if(userModel.isLoginWithImei()) {
			 msisdn = this.getMsisdn(userModel.getImei());
			 userModel.setCellphoneNumber(msisdn);
		} 
		this.validateCellphoneNumber(userModel.getCellphoneNumber());
		if(!userModel.isLoginWithImei()) {
			//Registrar usuario en acrobits
			registeredInacrobits =this.registerAcrobitsUser(userModel);
		}
        User user = UserMapper.convertUserModelToUser(userModel);
        Date date = new Date(Calendar.getInstance().getTime().getTime());
        user.setCreatedAt(date);
        user.setActive(true);
        user.setPassword(myPasswordEncoder.encode(user.getPassword()));
        Role role = getDefaultFinaluserRole();
        user.setRole(role);
        user.setRegisteredInacrobits(registeredInacrobits);
        User newUser = userRepository.save(user);
        this.createPhone(userModel, newUser);
        return newUser;
    }
	
	public void createPhone(UserModel userModel, User user) {
		 Phone phone = new Phone();
	        phone.setCellphoneNumber(userModel.getCellphoneNumber());
	        phone.setActive(true);
	        phone.setCreatedAt(new Date());
	        phone.setImei(userModel.getImei());
	        phone.setUser(user);
	        phoneRepository.save(phone);
	}
    
	@Transactional
    public User update(UserModel userModel) throws TrException {
    	User userUpdated = null;
    	if(userModel.getId() != null) {
    		Optional<User> optionalUser = findById(userModel.getId());
        	if (!optionalUser.isPresent())
                throw new TrException(entityName + " usuario no encontrado.");
        	User user = optionalUser.get();
        	user.setName(userModel.getName());
        	user.setLastName(userModel.getLastName());
        	user.setSecodLastName(userModel.getSecondLastName());
        	user.setEmail(userModel.getEmail());
            Date date = new Date(Calendar.getInstance().getTime().getTime());
            user.setUpdatedAt(date);
            if(userModel.isUpdatePassword() &&  StringUtils.isNotBlank(user.getPassword()) ) {
            	user.setPassword(myPasswordEncoder.encode(userModel.getPassword()));
                user.setPasswordUpdatedAt(date);
            }
            user = userRepository.save(user);
    	}
    	return userUpdated;
    }
    
	@Transactional
	public void updatePassword(UserModel userModel) throws TrException {
		Phone phone = phoneRepository.findByCellphoneNumber(userModel.getCellphoneNumber());
		User user = phone.getUser();
		if (user == null) {
			throw new TrException(entityName + " usuario no encontrado.");
		}
		Date date = new Date(Calendar.getInstance().getTime().getTime());
		user.setUpdatedAt(date);
		user.setPassword(myPasswordEncoder.encode(userModel.getPassword()));
		user.setPasswordUpdatedAt(date);
		user = userRepository.save(user);
		desableLinkConfirmationPassword(user);
	}
    
    public Role getDefaultFinaluserRole() {
    	Role role = new Role();
    	role.setId(RoleConstants.FINAL_USER_ID);
    	return role;
    }

    public void cleanUser(UserModel userModel) {
    	userModel.setName("");
    	userModel.setLastName("");
    	userModel.setSecondLastName("");
    	userModel.setCellphoneNumber("");
    	userModel.setEmail("");
    	userModel.setPassword("");
    	userModel.setPasswordConfirmation("");
    	userModel.setImei("");
    	userModel.setLoginWithImei(false);
    	userModel.setSaved(true);
    }

	public void validateCellphoneNumberWithCdr(String cellphoneNumber) throws TrException {
		boolean isValid = consumptionDetailService.validateMsisdn(cellphoneNumber);
		if (isValid) {
			User existingUser = findByUserName(cellphoneNumber);
			if (existingUser != null) {
				throw new TrException("El número " + cellphoneNumber + " ya se encuentra registrado");
			}
		} else {
			throw new TrException("El número " + cellphoneNumber + " no pertenece a Turbored"); // TODO MENSAJE
		}
	}
	
	private String getMsisdn(String imei) throws Exception {
		String msisdn = getCellphoneNumberByImei(imei);
    	if(msisdn == null  || !GeneralValidator.validatePattern(UserConstants.REGEX_CELLPHONE_NUMBER, msisdn)){
    		throw new TrException("No se pudo validar el imei " + imei + " en Turbored"); // TODO VERIFICAR QUE HACER SI NO REGRESA MSISDN
    	}
		return msisdn;
	}

    public void validatePasswords(UserModel userModel) throws TrException {
        if (StringUtils.isBlank(userModel.getPassword()) || StringUtils.isBlank(userModel.getPasswordConfirmation())) {
            throw new TrException("La contraseña o confirmación de contraseña  no pueden estar vacías.");
        }

        if (!userModel.getPassword().equals(userModel.getPasswordConfirmation())) {
            throw new TrException("La contraseña y confirmación de contraseña no coinciden.");
        }
    }
    
    @Transactional
    public void reestablishPassword(UserModel userModel) throws TrException, MessagingException, IOException {
    	User user = userRepository.findByUserNameAndEmail(userModel.getUserName(), userModel.getEmail());
    	if(user == null) {
    		throw new TrException("No existe registro para los datos ingresados.");
    	} else {
    		desableLinkConfirmationPassword(user); // Si existe algun link anterior se deshabilita
    		ConfirmationToken confirmationToken = new ConfirmationToken(user);
    		confirmationTokenRepository.save(confirmationToken);
    		this.sendReestablishPasswordEmailNotification(user.getEmail(), "Restablecimiento de contraseña", 
    				turboConsumptionWebUrl + CONFIRM_RESET_PASSWORD
    				 + "?token="
    				 +confirmationToken.getConfirmationToken());
    	}
    }
    
  public void sendReestablishPasswordEmailNotification(String toEmail, String subject, String messageStr) throws MessagingException, IOException {
	  logger.info("Enviando correo para restablecer password " + toEmail);
		if (StringUtils.isNotBlank(toEmail)) {
			Context ctx = new Context();
			  ctx.setVariable("token", messageStr);
			String template = templateEngine.process("email/recoverPassworEmailTemplate", ctx);
			try {
				emailService.sendEmailWithTemplateWs(toEmail, "Restablecer contraseña", template);
			} catch (Exception e) {
				logger.error("No pudo ser enviado el correo de restablecimiento password a " + toEmail
						+" Error msg " + e.getMessage());
			}
		} else {
			logger.info("Es requerido el email para enviar  el link de recuperación de contrseña por correo");
		}
}
    
    public void desableLinkConfirmationPassword(User user ) {
    	ConfirmationToken ct = confirmationTokenRepository.findByUserIdAndActive(user.getId(), true);
    	if(ct != null) {
    		ct.setActive(false);
    		confirmationTokenRepository.save(ct);
    	}
    }
    
    public ConfirmationToken findByConfirmationToken(String  confirmationToken, boolean active) {
    	return confirmationTokenRepository.findByConfirmationTokenAndActive(confirmationToken, active);
    }
    
    private boolean registerAcrobitsUser(UserModel userModel) {
    	logger.info("Procesando registro en acrobits para el usuario  a usuario " + userModel.getCellphoneNumber());
    	boolean registered = false;
    	if(activateAcrobits) {
    		try {
        		if(userModel != null) {
                	AcrobitsUserRequest acrobitUserRequest = new AcrobitsUserRequest();
                	acrobitUserRequest.setCloud_id(cloudId);
                	acrobitUserRequest.setEmail(userModel.getEmail());
                	acrobitUserRequest.setFirst_name(userModel.getName());
                	acrobitUserRequest.setLast_name(userModel.getLastName());
                	acrobitUserRequest.setMother_last_name(userModel.getSecondLastName());
                	acrobitUserRequest.setPassword(userModel.getPassword());
                	acrobitUserRequest.setUsername(userModel.getCellphoneNumber());
                	logger.info("Empezando a registrar a usuario en acrobits" + userModel.getCellphoneNumber() + "en acrobits");
                	acrobitUserClient.register(acrobitUserRequest);
                	registered = true;
                	logger.info("Usuario registrado con exito en acrobits:  " + userModel.getCellphoneNumber() + " completado");
            	}
        	} catch(Exception e) {
        		logger.error("No se pudo registrar el usuario en acrobits " + e.getMessage());
        	}
    	}
    	return registered;
    }
    
    @Override
    protected BaseRepository<User, Long> getRepository() {
        return this.userRepository;
    }
    
    private String getCellphoneNumberByImei(String imei) throws Exception {
    	String msisdn = gategayApiClient.getDeviceInformation(imei);
    	return msisdn;
    }
    
	private void validateCellphoneNumber(String cellphoneNumber) throws TrException {
		Boolean isTurbored = gategayApiClient.isTurboredMsisdn(cellphoneNumber);
		if (isTurbored == null) {
			throw new TrException("El número " + cellphoneNumber + " no pudo ser validado");
		} else if (isTurbored) {
			User existingUser = findByUserName(cellphoneNumber);
			Phone phone = phoneRepository.findByCellphoneNumber(cellphoneNumber);
			if (existingUser != null || phone != null) {
				throw new TrException("Ya existe una cuenta registrada con el número ingresado");
			}
		} else {
			throw new TrException("El número " + cellphoneNumber + " no pertenece a Turbored"); // TODO MENSAJE
		}
	}

}