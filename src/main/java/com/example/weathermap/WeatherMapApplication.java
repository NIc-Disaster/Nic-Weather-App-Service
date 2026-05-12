package com.example.weathermap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherMapApplication.class, args);
    }
}
