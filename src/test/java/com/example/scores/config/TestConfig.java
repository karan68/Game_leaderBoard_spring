package com.example.scores.config;

import com.example.scores.repository.AdminRepository;
import com.example.scores.repository.MockAdminRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public AdminRepository adminRepository() {
        return new MockAdminRepository();
    }
}