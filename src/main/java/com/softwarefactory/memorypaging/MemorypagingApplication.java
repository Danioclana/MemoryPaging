package com.softwarefactory.memorypaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MemorypagingApplication {

	//mvn spring-boot:run
	public static void main(String[] args) {
		SpringApplication.run(MemorypagingApplication.class, args);
		try {
			System.out.println("\n\n\033[0;32m" + "http://localhost:8080/" + "\033[0m");
		} catch (Exception e) {
			System.out.println("\033[0;31m" + "Erro ao iniciar o backend" + "\033[0m");
			System.out.println(e);
		}
	}

}
