package com.itb.inf2fm.projetoback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Aplicação Spring Boot para gerenciamento de clientes e técnicos
 * 
 * Endpoints disponíveis:
 * - /cliente - Operações CRUD de clientes
 * - /tecnico - Operações CRUD de técnicos
 * - /usuario - Gerenciamento de usuários
 * - /regiao - Gerenciamento de regiões
 * - /especialidade - Gerenciamento de especialidades
 * - /admin/servicos - Gerenciamento de serviços (Admin)
 * 
 * Integração Frontend:
 * - Flutter: Use http/dio package
 * - React/Vue: Use axios/fetch
 */
@SpringBootApplication
@org.springframework.data.jpa.repository.config.EnableJpaRepositories
public class ProjetobackApplication {

	private static final Logger log = LoggerFactory.getLogger(ProjetobackApplication.class);

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(ProjetobackApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void logApplicationStartup() {
		try {
			String protocol = "http";
			if (env.getProperty("server.ssl.key-store") != null) {
				protocol = "https";
			}
			String serverPort = env.getProperty("server.port", "8082");
			String contextPath = env.getProperty("server.servlet.context-path", "");
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			String appName = env.getProperty("spring.application.name", "ProjetoBack API");
			
			log.info("\n----------------------------------------------------------\n\t" +
					"O {} está rodando tudo certo :3\n\t" +
					"Localhost: \t{}://localhost:{}{}\n\t" +
					"Externo: \t{}://{}:{}{}\n\t" +
					"Swagger: \t{}://localhost:{}{}/swagger-ui.html\n\t" +
					"Profile(s): \t{}\n" +
					"----------------------------------------------------------",
					appName,
					protocol,
					serverPort,
					contextPath,
					protocol,
					hostAddress,
					serverPort,
					contextPath,
					protocol,
					serverPort,
					contextPath,
					env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
			);
		} catch (UnknownHostException e) {
			log.warn("The host name could not be determined, using `localhost` as fallback");
		}
	}
}
