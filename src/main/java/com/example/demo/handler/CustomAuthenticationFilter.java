package com.example.demo.handler;

//import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		setAuthenticationManager(authenticationManager);
		setFilterProcessesUrl("/authenticate");
	}
	public Authentication attemptAuthentication(HttpServletRequest request,HttpServletResponse response) {
		//Authentication authentication = super.attemptAuthentication(request, response);
		//System.out.println("Authorities: " + authentication.getAuthorities());
		String email=request.getParameter("email");
		String password=request.getParameter("password");
		String userType=request.getParameter("userType");
		System.out.println("@@@@@@@@");
		System.out.println("Email: "+email+" password: "+password+"role: "+userType);
		if(email == null || password == null ||  userType == null) {
			throw new  BadCredentialsException("Invalid login details");
		}
		//String role ="ROLE_"+userType.toUpperCase();
		//List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
		String finalName=userType+":"+email;
		//String finalName=email;
		UsernamePasswordAuthenticationToken authRequest=new UsernamePasswordAuthenticationToken(finalName,password/*,authorities*/);
		System.out.println("ContextHolder");
		SecurityContextHolder.getContext().setAuthentication(authRequest);
		setDetails(request,authRequest);
		System.out.println("Authentication Request: " + authRequest);
		//System.out.println("User " + email + " has roles: " + authorities);
		return this.getAuthenticationManager().authenticate(authRequest);
		
		
		
	}
}
