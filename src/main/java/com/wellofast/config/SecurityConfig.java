package com.wellofast.config;

import com.wellofast.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomUserDetailsService uds;
    public SecurityConfig(CustomUserDetailsService uds) { this.uds = uds; }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authProvider())
            .authorizeHttpRequests(a -> a
                .requestMatchers("/","/login","/register","/css/**","/js/**","/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/staff/**","/payroll/**").hasAnyRole("ADMIN")
                .requestMatchers("/patients/**","/appointments/**","/certificates/**").hasAnyRole("ADMIN","DOCTOR")
                .requestMatchers("/leaves/**").hasAnyRole("ADMIN","DOCTOR","EMPLOYEE")
                .requestMatchers("/portal/**").hasRole("PATIENT")
                .requestMatchers("/doctor/**").hasRole("DOCTOR")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(f -> f.loginPage("/login").loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard",true).failureUrl("/login?error=true").permitAll())
            .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll())
            .csrf(c -> c.ignoringRequestMatchers("/api/**"));
        return http.build();
    }
}
