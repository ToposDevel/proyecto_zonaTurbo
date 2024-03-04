package com.topostechnology.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.topostechnology.service.UserService;


@Controller
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Resource
    private UserService userService;
    
    @RequestMapping("/login")
    public String load(Model model) {
    	logger.info("Entrando a Login user");
        return "login";
    }
    
    @RequestMapping("/login-error/bad-credentials")
    public String badCredentials(Model model) {
        model.addAttribute("loginErrorMessage", "Usuario o contraseña inválida.");
        return "login";
    }
    
}
