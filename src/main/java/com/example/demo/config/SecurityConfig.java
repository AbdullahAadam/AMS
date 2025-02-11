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
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
//import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.example.demo.handler.CustomAuthenticationFailureHandler;
import com.example.demo.handler.CustomAuthenticationFilter;
import com.example.demo.handler.CustomAuthenticationSuccessHandler;
import com.example.demo.handler.SessionDebugFilter;

import jakarta.servlet.http.HttpServletRequest;

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
    		    .ignoringRequestMatchers("/admin/login", "/professor/login", "/student/login","/authenticate","/logout","/forgot","/reset","/admin/reset")
    		    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())	
    		)

//    	 .csrf(csrf -> csrf
//    	            .ignoringRequestMatchers("/authenticate")  // Disable CSRF for authenticate endpoint
//    	        )
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                .requestMatchers( "/admin/login", "/professor/login", "/student/login","/css/**","/js/**","/images/**","/admin/forgot","/admin/reset").permitAll()
                	.requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/professor/**").hasRole("PROFESSOR")
                    .requestMatchers("/student/**").hasRole("STUDENT")
                   
                    .anyRequest().authenticated()
                    
            )
            .sessionManagement(session ->session
            		.sessionFixation().migrateSession()
            		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .addFilterBefore((request,response,chain)->{
            	System.out.println("SecurityContext before filter chain: "+SecurityContextHolder.getContext().getAuthentication());
            	System.out.println("JSESSIONID: "+((HttpServletRequest)request).getSession().getId());
            	chain.doFilter(request, response);
            },UsernamePasswordAuthenticationFilter.class)
            //.addFilterBefore(new SecurityContextHolderFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new SessionDebugFilter(),  SecurityContextHolderFilter.class)
            
            .logout(logout ->
                logout
                    .logoutUrl	("/logout")
                    
                   .logoutSuccessHandler((request,response,authentication)->{
                    	System.out.println("autentication role for logout: "+authentication.getAuthorities());
                    	String role=authentication.getAuthorities().toString();
                    	if(role.contains("ROLE_ADMIN")) {
                    		response.sendRedirect("/admin/login?logout=true");
                    	}else if(role.contains("ROLE_PROFESSOR")) {
                    		response.sendRedirect("/professor/login?logout=true");
                    		
                    	}else if(role.contains("ROLE_STUDENT")) {
                    		response.sendRedirect("/student/login?logout=true");
                    	}else {
                    		response.sendRedirect("/login?logout=true");
                    	}
                    })
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID","XSRF-TOKEN")
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