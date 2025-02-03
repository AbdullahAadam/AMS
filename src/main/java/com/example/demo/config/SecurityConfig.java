package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.handler.CustomAuthenticationFailureHandler;
import com.example.demo.handler.CustomAuthenticationFilter;
import com.example.demo.handler.CustomAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.example.demo.service.CustomUserDetailsService;


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true)

public class SecurityConfig {
	
	private static final Logger logger  = LoggerFactory.getLogger(SecurityConfig.class);
	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Autowired
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	
    public SecurityConfig(CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, 
            CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
    		this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }
	//private final CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService();
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
		
	}
   // private final PasswordEncoder passwordEncoder;	

//    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        this.userDetailsService = userDetailsService;
//        this.passwordEncoder = passwordEncoder;
//    }

   

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter=new CustomAuthenticationFilter(authenticationManager);
        customAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        customAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        
    	logger.info("Initializing Security Configuration");
    	http
    	
    	//.csrf(csrf->csrf.disable())
    	.csrf(csrf -> csrf
    		    .ignoringRequestMatchers("/admin/login", "/professor/login", "/student/login","/authenticate")
    		)

//    	 .csrf(csrf -> csrf
//    	            .ignoringRequestMatchers("/authenticate")  // Disable CSRF for authenticate endpoint
//    	        )
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                .requestMatchers( "/admin/login", "/professor/login", "/student/login","/css/**","/js/**","/images/**").permitAll()
                	.requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/professor/**").hasRole("PROFESSOR")
                    .requestMatchers("/student/**").hasRole("STUDENT")
                   
                    .anyRequest().authenticated()
                    
            )
            .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            );
    	
        logger.info("Security Filter Chain Cofigured Successfully");    
        return http.build(); 
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}   