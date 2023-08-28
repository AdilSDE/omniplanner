package com.microd.imagegenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class ImageGeneratorApplication /*extends SpringBootServletInitializer*/ {

	public static void main(String[] args) {

        SpringApplication.run(ImageGeneratorApplication.class, args);
	}
}
