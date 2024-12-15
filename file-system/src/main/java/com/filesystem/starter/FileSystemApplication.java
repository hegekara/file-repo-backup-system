package com.filesystem.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.filesystem"})
@EntityScan(basePackages = {"com.filesystem"})
@ComponentScan(basePackages = {"com.filesystem"})
@EnableJpaRepositories(basePackages = {"com.filesystem"})
public class FileSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileSystemApplication.class, args);
	}

}
