package com.example.demo.controller;

import com.example.demo.Util.GetInfoUser;
import com.example.demo.model.Dto.JwtRequest;
import com.example.demo.model.Dto.UserRequestDto;
import com.example.demo.service.serviceImpl.JwtUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*
Expose a POST API /authenticate using the JwtAuthenticationController. The POST API gets username and password in the
body- Using Spring Authentication Manager we authenticate the username and password.If the credentials are valid,
a JWT token is created using the JWTTokenUtil and provided to the client.
 */

@Slf4j
@RestController


public class JwtAuthenticationController {
    private final JwtUserDetailsService userDetailsService;


    public JwtAuthenticationController(JwtUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserRequestDto user) throws Exception {
        return userDetailsService.save(user);
    }
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        return userDetailsService.login(authenticationRequest);
    }
}
