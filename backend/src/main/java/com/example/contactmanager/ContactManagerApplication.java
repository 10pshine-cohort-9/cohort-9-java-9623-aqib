package com.example.contactmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ContactManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactManagerApplication.class, args);
    }
}
