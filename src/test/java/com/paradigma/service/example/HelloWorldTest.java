package com.paradigma.service.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HelloWorldApplication.class)
@WebIntegrationTest({ "server.port=8081" })
public class HelloWorldTest {
	public String baseUrl = "http://localhost:8081/api/v1";
	public RestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void loginTest() {
		String token = login("Javier", "abcd123");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		List<String> tokenList = new ArrayList<>();
		tokenList.add(token);
		headers.put("X-AUTH-TOKEN", tokenList);
		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<String> resultado = restTemplate.exchange(baseUrl + "/hi", HttpMethod.GET, request,
				String.class);
		Assert.assertEquals(resultado.getBody(), "Hello World!");
	}

	@Test
	public void listUsersTest() {
		String token = login("Javier", "abcd123");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		List<String> tokenList = new ArrayList<>();
		tokenList.add(token);
		headers.put("X-AUTH-TOKEN", tokenList);
		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<List> resultado = restTemplate.exchange(baseUrl + "/users", HttpMethod.GET, request, List.class);
		Model model = new Model("5", "denny");
		Assert.assertTrue(!resultado.getBody().contains(model));
		Assert.assertTrue(resultado.getBody().size() == 2);
	}

	@Test
	public void listOneUserTest() {
		String token = login("Javier", "abcd123");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		List<String> tokenList = new ArrayList<>();
		tokenList.add(token);
		headers.put("X-AUTH-TOKEN", tokenList);
		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<Model> resultado1 = restTemplate.exchange(baseUrl + "/users/1", HttpMethod.GET, request,
				Model.class);

		ResponseEntity<Model> resultado5 = restTemplate.exchange(baseUrl + "/users/5", HttpMethod.GET, request,
				Model.class);
		Assert.assertNotNull(resultado1.getBody());
		Assert.assertEquals(resultado5.getStatusCode(), HttpStatus.FORBIDDEN);

	}

	@Test
	public void dennyAccessNotValidTokenTest() {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<String> resultado = restTemplate.exchange(baseUrl + "/hi", HttpMethod.GET, request,
				String.class);
		Assert.assertEquals(resultado.getStatusCode(), HttpStatus.FORBIDDEN);
	}

	private String login(String username, String password) {
		Map<String, Object> user = new HashMap<>();
		user.put("username", username);
		user.put("password", password);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(user);

		ResponseEntity<String> resultado = restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, entity,
				String.class);
		String token = resultado.getHeaders().getFirst("X-AUTH-TOKEN");
		return token;
	}
}