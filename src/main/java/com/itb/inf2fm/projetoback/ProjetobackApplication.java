// Classe principal do projeto Spring Boot
// Ponto de entrada da aplicação backend
// Integração com Frontend: todos os endpoints REST são expostos a partir desta aplicação
// Para Flutter/ReactJS, configure o endereço e porta do backend para consumir os endpoints

package com.itb.inf2fm.projetoback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetobackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetobackApplication.class, args);
	}
}
