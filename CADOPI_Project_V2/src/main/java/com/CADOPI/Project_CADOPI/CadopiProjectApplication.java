package com.CADOPI.Project_CADOPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@SpringBootApplication
public class CadopiProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CadopiProjectApplication.class, args);
	}
}
