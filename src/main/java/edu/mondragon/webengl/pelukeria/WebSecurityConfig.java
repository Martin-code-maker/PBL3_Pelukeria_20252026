package edu.mondragon.webengl.pelukeria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.service.ErabiltzaileaService;

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

	@Autowired
	private ErabiltzaileaService erabiltzaileaService;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// @formatter:off
		http
			.authorizeHttpRequests((requests) -> requests
				// IMPORTANTE: Permitir acceso a recursos estáticos (CSS, JS, imágenes)
				.requestMatchers("/", "/home", "/login", "/register", 
								"/css/**", "/js/**", "/images/**", "/favicon.ico",
								"/pelukeria/**","/webjars/**").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.defaultSuccessUrl("/home", true)  // Redirige a home después de login exitoso
				.permitAll()
			)
			.logout((logout) -> logout
				.logoutSuccessUrl("/home?logout")  // Redirige a home después de logout
				.permitAll()
			)
			.csrf(csrf -> csrf.disable())
			.userDetailsService(erabiltzaileaService);
			
		// @formatter:on

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}