package com.example.demoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class DemoApplication {

    private DemoApplication() {
        // Private constructor to hide implicit public one
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}