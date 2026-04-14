package com.afrunt.jach.api;

import com.afrunt.jach.ACH;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JachApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(JachApiApplication.class, args);
    }

    @Bean
    public ACH ach() {
        return new ACH();
    }
}
