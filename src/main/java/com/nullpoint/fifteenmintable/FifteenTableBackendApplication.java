package com.nullpoint.fifteenmintable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FifteenTableBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FifteenTableBackendApplication.class, args);
    }

}
