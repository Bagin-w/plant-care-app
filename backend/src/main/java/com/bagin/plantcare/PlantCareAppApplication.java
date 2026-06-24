package com.bagin.plantcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlantCareAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantCareAppApplication.class, args);
	}
}