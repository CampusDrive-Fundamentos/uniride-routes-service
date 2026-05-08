package com.uniride.unirideroutesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UnirideRoutesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnirideRoutesServiceApplication.class, args);
    }
}