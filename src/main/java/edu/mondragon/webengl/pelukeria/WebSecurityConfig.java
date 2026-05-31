package edu.mondragon.webengl.pelukeria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.service.ErabiltzaileaService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // necesario para que funcione @PreAuthorize en los controllers
class WebSecurityConfig {

    @Autowired
    private ErabiltzaileaService erabiltzaileaService;
 
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/", "/home", "/login", "/register",
                                 "/css/**", "/js/**", "/images/**", "/favicon.ico",
                                 "/pelukeria/**", "/webjars/**",
                                 "/uploads/**", "/reservas/test-email").permitAll()
                .requestMatchers("/ws-chat/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/home?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable())
            .userDetailsService(erabiltzaileaService);
 
        return http.build();
    }
 
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}