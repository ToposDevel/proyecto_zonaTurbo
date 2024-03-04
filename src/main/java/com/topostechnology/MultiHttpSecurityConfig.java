package com.topostechnology;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class MultiHttpSecurityConfig {

	@Configuration
	@Order(1)
	public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable().antMatcher("/webhook/**").authorizeRequests().anyRequest().permitAll();
		}
	}

	@Configuration
	@Order
	public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests()
			.antMatchers("/adminTurboOffice").access("hasAnyAuthority('ZONATURBO_USER', 'TURBO_OFFICE_ADMIN')")
			.antMatchers("/adminTurboOfficeUser/{virtualNumber}").access("hasAnyAuthority('ZONATURBO_USER', 'TURBO_OFFICE_ADMIN')")
			.antMatchers("/turboOffice/validateIdentity").access("hasAnyAuthority('ZONATURBO_USER', 'TURBO_OFFICE_ADMIN')")
					.antMatchers("/compatibility/").permitAll()
					.antMatchers("/apnChange/").permitAll()
					.antMatchers("/checkbalance/").permitAll()
					.antMatchers("/turboOffice/phoneImei").permitAll()
					.antMatchers("/turboOffice/extraRecharge").permitAll()
					.antMatchers("/turboOffice/activation/paymentLink").permitAll()
					.antMatchers("/turboOffice/register").permitAll()
					.antMatchers("/conekta/cardPayment/pay/").permitAll()
					.antMatchers("/recharge/expressPlusRechargeHbb/").permitAll()
					.antMatchers("/recharge/expressPlusRechargeMifi/").permitAll()
					.antMatchers("/recharge/expressPlusRechargeMov").permitAll()
					.antMatchers("/recharge/expressPlusRecharge/").permitAll()
					.antMatchers("/recharge/expressPlusRecharge/plans/{celphoneNumberOrImei}").permitAll()
					.antMatchers("/conekta/payment/paymentMethods/").permitAll().antMatchers("/conekta/oxxoCash/")
					.permitAll().antMatchers("/porting/status/").permitAll()
					.antMatchers("/recharge/expressRecharge/plans/{celphoneNumber}").permitAll()
					.antMatchers("/recharge/expressRecharge/").permitAll()
					.antMatchers("/conekta/getConektaPublicToken").permitAll()
					.antMatchers("/conekta/subscription/validateCoverage/{coordinates}").permitAll()
					.antMatchers("/conekta/subscription/offers/{cellphoneNumber}").permitAll()
					.antMatchers("/conekta/subscription/offersMov/{cellphoneNumber}").permitAll()
					.antMatchers("/conekta/subscription/offersHbbMifi/{imei}").permitAll()
					.antMatchers("/conekta/subscription/imeiSubscription/").permitAll()
					.antMatchers("/recharge/expressPlusRechargeMovOxxo").permitAll()
					.antMatchers("/recharge/expressPlusRechargeMovPaynet").permitAll()
					.antMatchers("/conekta/payment/paymentMethodOxxoPaynet/").permitAll()

					
					.antMatchers("/recharge/expressPlusRechargePayment/").permitAll()
					.antMatchers("/recharge/payment").permitAll()
					
					.antMatchers("/conekta/subscription/cellphoneNumberSubscription/").permitAll()
					.antMatchers("/conekta/subscription/").permitAll().antMatchers("/porting/").permitAll()
					.antMatchers("/promoSim/validateImeiAndActivateSim/").permitAll()
					.antMatchers("/promoSim/activateSim/").permitAll().antMatchers("/promoSim/processActivateSim/")
					.permitAll().antMatchers("/promoSim/").permitAll().antMatchers("/promoSim/validateImei/")
					.permitAll().antMatchers("/coverage/").permitAll().antMatchers("/coverage/validateCoverage/")
					.permitAll().antMatchers("/coverage/getCoordinates/{address}").permitAll()
					.antMatchers("/coverage/coppel/").permitAll()
					.antMatchers("/users/newUser/").permitAll().antMatchers("/users/register/").permitAll()
					.antMatchers("/users/forgot-password/").permitAll().antMatchers("/users/confirm-reset").permitAll()
					.antMatchers("/users/reset-password/").permitAll().antMatchers("/login-error/*").permitAll()
					.antMatchers("/users/").denyAll().antMatchers("/users").denyAll().antMatchers("/**").authenticated()
					.and().formLogin().loginPage("/login").permitAll().failureHandler(authenticationFailureHandler())
					.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
					.logoutSuccessUrl("/login").and().exceptionHandling().accessDeniedPage("/accessDenied");
			
		}

		@Bean
		public static AuthenticationFailureHandler authenticationFailureHandler() {
			ExceptionMappingAuthenticationFailureHandler authenticationFailureHandler = new ExceptionMappingAuthenticationFailureHandler();
			Map<String, String> exMap = new HashMap<>();
			exMap.put("org.springframework.security.authentication.BadCredentialsException",
					"/login-error/bad-credentials");
			authenticationFailureHandler.setExceptionMappings(exMap);
			return authenticationFailureHandler;
		}

		@Bean
		public SessionRegistry sessionRegistry() {
			SessionRegistry sessionRegistry = new SessionRegistryImpl();
			return sessionRegistry;
		}

		@Override
		public void configure(WebSecurity web) {
			web.ignoring().antMatchers("/css/**");
			web.ignoring().antMatchers("/images/**");
			web.ignoring().antMatchers("/js/**");
			web.ignoring().antMatchers("/script/**");
		}

		@Autowired
		public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
		}

		@Autowired
		@Qualifier("customUserDetailsService")
		UserDetailsService userDetailsService;

		@Bean
		public PasswordEncoder encoder() {
			return new BCryptPasswordEncoder(11);
		}

	}

}