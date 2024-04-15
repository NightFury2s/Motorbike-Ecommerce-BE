package com.example.demo.repositories;

import com.example.demo.model.entity.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

}