package com.example.SADokobitGateway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.opencv.core.Core;

@Configuration
public class SystemLibLoader {
    @Bean
    public void init() {
        String opencvpath = System.getProperty("user.dir") + "\\files\\";
        System.load(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
        System.out.println(opencvpath + Core.NATIVE_LIBRARY_NAME + ".dll");
    }
}
