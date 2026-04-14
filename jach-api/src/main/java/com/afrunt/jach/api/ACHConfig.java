package com.afrunt.jach.api;

import com.afrunt.jach.ACH;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ACHConfig {

    @Bean
    public ACH ach() {
        return new ACH();
    }
}
