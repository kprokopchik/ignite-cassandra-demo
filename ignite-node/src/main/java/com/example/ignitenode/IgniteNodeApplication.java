package com.example.ignitenode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(locations = "classpath:/ignite-cassandra.xml")
@Slf4j
public class IgniteNodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(IgniteNodeApplication.class, args);
	}

}
