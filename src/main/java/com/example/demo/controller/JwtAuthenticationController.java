package com.example.demo.controller;


import com.example.demo.model.Dto.JwtRequest;
import com.example.demo.model.Dto.UserRequestDto;
import com.example.demo.service.CustomerService;
import com.example.demo.service.serviceImpl.JwtUserDetailsService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class JwtAuthenticationController {
    private final JwtUserDetailsService userDetailsService;
    private final CustomerService customerService;

    public JwtAuthenticationController(JwtUserDetailsService userDetailsService, CustomerService customerService) {
        this.userDetailsService = userDetailsService;
        this.customerService = customerService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveUser(@RequestBody UserRequestDto user) {
        return userDetailsService.register(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        return userDetailsService.login(authenticationRequest);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam("oldPassword") String oldPassword,
                                            @RequestParam("newPassword") String newPassword,
                                            @RequestParam("enterPassword") String enterPassword) {
        return customerService.changePassword(oldPassword, newPassword, enterPassword);
    }

    @PostMapping("/change-customer-information")
    public ResponseEntity<?> changeCustomerInformation(@RequestBody UserRequestDto userRequestDto) {
        return customerService.changeCustomerInformation(userRequestDto);
    }

    @GetMapping("/get-information-user")
    public ResponseEntity<?> getInformation() {
        return customerService.getInformation();
    }
}
