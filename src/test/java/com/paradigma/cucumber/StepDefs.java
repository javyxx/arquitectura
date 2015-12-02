package com.paradigma.cucumber;

import java.util.List;

import org.junit.Assert;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;

import com.paradigma.service.example.HelloWorldApplication;

import cucumber.api.Scenario;
import cucumber.api.java.Before;
import cucumber.api.java.es.Cuando;
import cucumber.api.java.es.Dado;
import cucumber.api.java.es.Entonces;

@ContextConfiguration(classes = HelloWorldApplication.class, loader = SpringApplicationContextLoader.class)
@WebIntegrationTest({ "server.port=8081" })
public class StepDefs extends AbstractDefs{
	public StepDefs(){
		
	}
	
	@Before
	public void exec(Scenario scenario){
	   	this.scenario = scenario;
		initContext();
	}
	
	@Dado("^el nombre de usuario \"([^\"]*)\" y contraseña \"([^\"]*)\"$")
	public void el_nombre_de_usuario_y_contraseña(String username, String password) throws Throwable {
		body.put("username", username.trim());
		body.put("password", password.trim());
	}

	@Cuando("^invoco la url \"([^\"]*)\" con el método \"([^\"]*)\"$")
	public void invoco_la_url_con_el_método(String url, String method) throws Throwable {
		HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());
		lastResponse = execute("http://localhost:8081/api/v1/" + url, httpMethod, toJson(body), param, String.class);

	}

	@Entonces("^el resultado debe ser un http status (\\d+)$")
	public void el_resultado_debe_ser_un_http_status(int status) throws Throwable {
		HttpStatus httpStatus = HttpStatus.valueOf(status);
		Assert.assertEquals(httpStatus, lastResponse.getStatusCode());
	}
	
	
}
