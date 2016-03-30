package com.paradigma.service.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;

import com.paradigma.arquitecture.ParadigmaApplication;
import com.paradigma.arquitecture.model.DataAccessMode;
import com.paradigma.arquitecture.model.SecurityMode;

@ParadigmaApplication(securityMode = SecurityMode.ALLOW_ALL, 
					  dataAccessMode = DataAccessMode.IN_MEMORY, 
					  enableAmqpConfig = true, 
					  enableCommander = true)
public class HelloWorldApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HelloWorldApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}
}
