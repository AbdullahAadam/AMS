package com.example.demo.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@Component
//public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//    	 request.getRequestDispatcher(request.getContextPath() + "/login?error=Invalid email or password").forward(request, response);;
//    }
//}
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) 
            throws IOException, ServletException {
        
        // Retrieve userType from request
        String userType = request.getParameter("userType");
        
        // Default redirect URL
        String redirectUrl = "/login?error=true";

        // Redirect based on userType
        if ("ADMIN".equals(userType)) {
            redirectUrl = "/admin/login?error=true";
        } else if ("PROFESSOR".equals(userType)) {
            redirectUrl = "/professor/login?error=true";
        } else if ("STUDENT".equals(userType)) {
            redirectUrl = "/student/login?error=true";
        }

        // Perform redirect
        response.sendRedirect(redirectUrl);
    }
}