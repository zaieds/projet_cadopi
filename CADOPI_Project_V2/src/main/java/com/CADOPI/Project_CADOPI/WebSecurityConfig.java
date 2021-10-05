package com.CADOPI.Project_CADOPI;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;


/*
cette annotation dit à spring security que c'est la web security configuration web
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${ldap.urls}")
	private String ldapUrls;

	@Value("${ldap.base.dn}")
	private String ldapBaseDn;

	@Value("${ldap.username}")
	private String ldapSecurityPrincipal;

	@Value("${ldap.password}")
	private String ldapPrincipalPassword;

	@Value("${ldap.user.dn.pattern}")
	private String ldapUserDnPattern;

	@Value("${ldap.enabled}")
	private String ldapEnabled;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers("/login**").permitAll()
				.antMatchers("/chargtOPI/**").fullyAuthenticated()
				.antMatchers("/").fullyAuthenticated()
				.and()
				.formLogin()
				.loginPage("/login")
				.failureUrl("/login?error")
				.permitAll()
				.and()
				.logout()
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.permitAll();
	}
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		if(Boolean.parseBoolean(ldapEnabled)) {
			auth
					.ldapAuthentication()
					.contextSource()
					.url(ldapUrls + ldapBaseDn)
					.and()
					.userDnPatterns(ldapUserDnPattern);
		} else {
			auth
					.inMemoryAuthentication()
					.withUser("user").password("password").roles("USER")
					.and()
					.withUser("admin").password("admin").roles("ADMIN");
		}
	}

}