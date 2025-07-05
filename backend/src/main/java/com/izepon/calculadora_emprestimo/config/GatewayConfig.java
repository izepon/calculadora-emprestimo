package com.izepon.calculadora_emprestimo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GatewayConfig {

    @Bean
    public WebMvcConfigurer gatewayConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/calculadora-emprestimo/calcular")
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowedOrigins("*");
            }
        };
    }
}
