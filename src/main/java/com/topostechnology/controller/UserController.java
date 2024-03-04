package com.topostechnology.controller;

import java.security.Principal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.topostechnology.domain.ConfirmationToken;
import com.topostechnology.domain.User;
import com.topostechnology.exception.TrException;
import com.topostechnology.mapper.UserMapper;
import com.topostechnology.model.UserModel;
import com.topostechnology.service.RoleService;
import com.topostechnology.service.UserService;
import com.topostechnology.utils.StringUtils;
import com.topostechnology.validation.UserValidator;

@Controller
@RequestMapping("/users")
public class UserController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
    private UserService userService;

    @Resource
    private RoleService roleService;
    
    @RequestMapping("/newUser")
    public ModelAndView newUser() {
    	logger.info("Registrar nuevo usuario");
        ModelAndView modelAndView = new ModelAndView("user/newUser");
        UserModel userModel = new UserModel();
        userModel.setSaved(false);
    	modelAndView.addObject("userForm", userModel);
        modelAndView.addObject("displayForm", true);
        modelAndView.addObject("formObject", addFormObject());
        return modelAndView;
    }
    
    @RequestMapping(value= "/register", method=RequestMethod.POST)
    public ModelAndView saveUser(ModelAndView modelAndView, @Valid @ModelAttribute("userForm") final UserModel userModel,
                                 BindingResult bindingResult, HttpServletRequest request, Errors errors){
    	logger.info("Creando nuevo usuario para el numero de celular " + userModel.getCellphoneNumber());
    	modelAndView.setViewName("user/newUser");
    	bindingResult = UserValidator.validate(userModel, bindingResult);
    	if(!bindingResult.hasFieldErrors()) {
    		try {
    			userService.create(userModel);
    			userService.cleanUser(userModel);
    			modelAndView.addObject("confirmationMessage", "Ha sido registrado exitosamente");
    			modelAndView.addObject("displayForm", false);
            } catch (TrException e) {
                bindingResult.reject("failedMessage", e.getMessage());
            } catch (Exception e) {
                logger.error("Error on saving user.", e);
                bindingResult.reject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde");
            }
    	}else {
    		for (Object object : bindingResult.getAllErrors()) {
    		    if(object instanceof FieldError) {
    		        FieldError fieldError = (FieldError) object;
    		        logger.error(fieldError.getCode());
    		    }

    		    if(object instanceof ObjectError) {
    		        ObjectError objectError = (ObjectError) object;

    		        logger.error(objectError.getCode());
    		    }
    		}
    	}
    	return modelAndView;
    }
    
	@RequestMapping("/editUser")
	public ModelAndView editUser(Principal principal) {
		logger.info("Editar  usuario");
		ModelAndView modelAndView = new ModelAndView("user/editUser");
		User user = userService.findByUserName(principal.getName());
		if (user != null) { // TODO agregar mensajae de error
			UserModel userModel = UserMapper.convertUserToUserModel(user);
			modelAndView.addObject("userForm", userModel);
			modelAndView.addObject("displayForm", true);
		}
		return modelAndView;
	}

    @RequestMapping(value="/update",  method=RequestMethod.POST)
	public ModelAndView updateUser(ModelAndView modelAndView,
			@Valid @ModelAttribute("userForm") final UserModel userModel, BindingResult bindingResult,
			HttpServletRequest request, Errors errors) {
		logger.info("Actualizando datos de usuario");
		modelAndView.addObject("displayForm", true);
		modelAndView = new ModelAndView("user/editUser");
		if (userModel.getId() != null) {
			try {
				if (userModel.isUpdatePassword() || StringUtils.isNotBlank(userModel.getPassword())) {
					userService.validatePasswords(userModel);
				}
				if (!bindingResult.hasFieldErrors()) {
					userService.update(userModel);
					userService.cleanUser(userModel);
					modelAndView.addObject("confirmationMessage", "Ha sido actualizado exitosamente");
					modelAndView.addObject("displayForm", false);
				}
			} catch (TrException e) {
				modelAndView.addObject("displayForm", true);
				bindingResult.reject("failedMessage", e.getMessage());
			} catch (Exception e) {
				modelAndView.addObject("displayForm", true);
				logger.error("Error on saving user.", e);
				bindingResult.reject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde");
			}
		} else {
			bindingResult.reject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		modelAndView.addObject("userForm", userModel);
		return modelAndView;
	}
    
    @RequestMapping(value="/forgot-password", method=RequestMethod.GET)
    public ModelAndView displayResetPassword(ModelAndView modelAndView, UserModel userForm) {
    	logger.info("Entrando a pantalla de  restablecimiento de contraseña");
        modelAndView.addObject("userForm", userForm);
        modelAndView.addObject("displayForm", true);
        modelAndView.setViewName("user/forgotPassword");
        return modelAndView;
    }

    @RequestMapping(value="/forgot-password", method=RequestMethod.POST)
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, @Valid @ModelAttribute("userForm") final UserModel userModel,
            BindingResult bindingResult, HttpServletRequest request, Errors errors){
    	logger.info("Solicitando restablecimiento de contraseña");
    	 modelAndView.setViewName("user/forgotPassword");
    	try {
    		userService.reestablishPassword(userModel);
    		modelAndView.addObject("confirmationMessage", "Se ha enviado un correo para restablecer su contraseña");
    		userService.cleanUser(userModel);
    		modelAndView.addObject("displayForm", false);
    	} catch (TrException e) {
			modelAndView.addObject("displayForm", true);
			bindingResult.reject("failedMessage", e.getMessage());
		} catch (Exception e) {
			modelAndView.addObject("displayForm", true);
			logger.error("Error on saving user.", e);
			bindingResult.reject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde");
		}
        return modelAndView;
    }
    
	@RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token")String confirmationToken, BindingResult bindingResult)
	{
		logger.info("Abriendo el link de restablecimiento de contraseña");
		boolean active = true;
		ConfirmationToken token = userService.findByConfirmationToken(confirmationToken, active);
		modelAndView.addObject("displayForm", true);
		if(token != null) {
			User user = token.getUser();
			UserModel userModel = UserMapper.convertUserToUserModel(user);
			modelAndView.addObject("userForm", userModel);
			modelAndView.setViewName("user/resetPassword");
		} else {
			modelAndView.setViewName("general");
			modelAndView.addObject("message", "El link es invalido.");
		}
		return modelAndView;
	}
	
	@RequestMapping(value = "/reset-password", method = RequestMethod.POST)
	public ModelAndView resetUserPassword(ModelAndView modelAndView, @Valid @ModelAttribute("userForm") final UserModel userModel,
            BindingResult bindingResult, HttpServletRequest request, Errors errors){
		modelAndView.setViewName("user/resetPassword");
		logger.info("Restableciendo contraseña");
		try {
			userService.validatePasswords(userModel);
			if (!bindingResult.hasFieldErrors()) {
				userService.updatePassword(userModel);
				userService.cleanUser(userModel);
				modelAndView.addObject("message", "la contraseña ha sido  restablecida exitosamente");
				modelAndView.addObject("displayForm", false);
				modelAndView.setViewName("general");
			}
		} catch (TrException e) {
			modelAndView.addObject("displayForm", true);
			bindingResult.reject("failedMessage", e.getMessage());
		} catch (Exception e) {
			modelAndView.addObject("displayForm", true);
			logger.error("Error on saving user.", e);
			bindingResult.reject("failedMessage", "La operación no pudo ser realizada, intente nuevamente más tarde");
		}
		return modelAndView;
	}
    
    protected User addFormObject() {
        return new User();
    }


}
