package com.example.demo.handler;

import java.io.IOException;
//import java.util.Collection;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
            throws IOException, ServletException {

        HttpSession session=request.getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT",SecurityContextHolder.getContext());
        System.out.println("Session ID after Login: "+session.getId());
        System.out.println("Session Authentication in Session: "+SecurityContextHolder.getContext().getAuthentication());
    	System.out.println("@@@@ Authentication Successful for: " + authentication.getName());
        System.out.println("Authorities after successful login: " + authentication.getAuthorities());
       
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            System.out.println("@@@ Assigned Role: " + authority.getAuthority());  // Check assigned roles
        }

        
        Authentication authInContext = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("ContextHolder");
        System.out.println("Authentication in SecurityContext: " + authInContext);

        String redirectUrl = "/login"; 

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            switch (role) {
                case "ROLE_ADMIN":
                    redirectUrl = "/admin/dashboard";
                    break;
                case "ROLE_PROFESSOR":
                    redirectUrl = "/professor/dashboard";
                    break;
                case "ROLE_STUDENT":
                    redirectUrl = "/student/dashboard";
                    break;
                default:
                    System.out.println("No valid role assigned!");
                    redirectUrl = "/login?error=Invalid role";
            }
        }
        
        System.out.println("Redirecting to: " + request.getContextPath() + redirectUrl);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}




/*@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) 
            throws IOException, ServletException {
    	System.out.println("@@@@ Authentication Successful for: " + authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            System.out.println("@@@ Assigned Role: " + authority.getAuthority());
        }
        String redirectUrl = "/login"; 

        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            
            if (role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (role.equals("ROLE_PROFESSOR")) {
                redirectUrl = "/professor/dashboard";
                break;
            } else if (role.equals("ROLE_STUDENT")) {
                redirectUrl = "/student/dashboard";
                break;
            }else {
            	System.out.println(" No valid role assigned!");
                redirectUrl = "/login?error=Invalid role";
            }
        }

        System.out.println("Redirecting"+redirectUrl);
        response.sendRedirect(request.getContextPath() + redirectUrl);
        System.out.println("Authentication in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());

    }
}
*/




/*@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();
        String userType = username.split(":")[0]; 

        if ("admin".equals(userType)) {
            response.sendRedirect("/admin/dashboard");
        } else if ("professor".equals(userType)) {
            response.sendRedirect("/professor/dashboard");
        } else {
            response.sendRedirect("/student/dashboard");
        }
        System.out.println("Username"+username);
        System.out.println("Role"+authentication.getAuthorities());
        
    }
}
*/





/*@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
                return;
            } else if (authority.getAuthority().equals("ROLE_PROFESSOR")) {
                response.sendRedirect("/professor/dashboard");
                return;
            } else if (authority.getAuthority().equals("ROLE_STUDENT")) {
                response.sendRedirect("/student/dashboard");
                return;
            }
        }
    }
}*/
