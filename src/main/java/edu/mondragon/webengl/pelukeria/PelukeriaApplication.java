package edu.mondragon.webengl.pelukeria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PelukeriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PelukeriaApplication.class, args);
	}

}
