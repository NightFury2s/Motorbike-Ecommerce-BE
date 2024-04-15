package com.example.demo.controller;

import com.example.demo.Util.SendSMS;
import com.example.demo.model.Dto.sms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @PostMapping("/sendSms")
    public ResponseEntity<?> sendSms(@RequestBody sms smsRequest) {
        return SendSMS.sendSms(smsRequest);
    }
}