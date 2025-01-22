package com.dash.StockAnalysis.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.web.servlet.config.annotation.CorsRegistry;


@Configuration
public class CorsConfig implements WebMvcConfigurer{
	 @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        registry.addMapping("/**")  // Allow all paths
	                .allowedOrigins("http://localhost:3000")  // Allow React app from this origin
	                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow these HTTP methods
	                .allowedHeaders("*")  // Allow any headers
	                .allowCredentials(true);  // If you need cookies/auth headers
	    }
	 
	 @Bean
	    public ObjectMapper objectMapper() {
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.registerModule(new JavaTimeModule());
	        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	        return objectMapper;
	    }
	
}
