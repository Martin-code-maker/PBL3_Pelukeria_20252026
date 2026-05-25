package edu.mondragon.webengl.pelukeria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
				.requestMatchers("/", "/home", "/login", "/demo/**", "/pelukeria/**").permitAll()
				.anyRequest().authenticated()
			)
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout(LogoutConfigurer::permitAll)
			.csrf(csrf -> csrf.disable()) // IMPORTANTE: Desactiva CSRF para pruebas con curl
			.userDetailsService(erabiltzaileaService);
			
		// @formatter:on

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	/*
	@Bean
	UserDetailsService userDetailsService(PasswordEncoder encoder) {
		String password = encoder.encode("password");
		UserDetails user = User.withUsername("user").password(password).roles("USER").build();
		return new InMemoryUserDetailsManager(user);
	}
	 */
}