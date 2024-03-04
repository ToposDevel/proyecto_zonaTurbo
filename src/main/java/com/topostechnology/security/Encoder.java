package com.topostechnology.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Encoder {
	
    @Bean(name="myPasswordEncoder")
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }


}
