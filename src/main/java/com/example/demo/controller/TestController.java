package com.example.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

public class TestController {
	@GetMapping("/whoami")
	@ResponseBody
	public String whoAmI(@AuthenticationPrincipal UserDetails userDetails) {
	    if (userDetails == null) {
	        return "No user is authenticated";
	    }
	    return "Authenticated user: " + userDetails.getUsername() + " with roles: " + userDetails.getAuthorities();
	}

}
