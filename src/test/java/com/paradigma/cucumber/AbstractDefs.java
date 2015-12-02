package com.paradigma.cucumber;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paradigma.arquitecture.util.Constants;

import java.net.HttpRetryException;

import cucumber.api.Scenario;

public class AbstractDefs {
	protected ResponseEntity<String> lastResponse = null;

	protected TestRestTemplate restTemplate = new TestRestTemplate();

	protected Map<String, Object> param = new HashMap<>();
	protected Map<String, Object> body = new HashMap<>();

	protected ObjectMapper objectMapper = new ObjectMapper();
	protected Scenario scenario;
	protected String accessToken;

	@Autowired
	protected ConversionService conversionService;


	protected void initContext() {
		param.clear();
		body.clear();
		lastResponse = null;
		accessToken = null;
	}

	protected <T> ResponseEntity<T> execute(String url, HttpMethod method, Object body, Map<String, Object> param,
			Class responseType) {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.set("Accept", "application/json");
		if (accessToken != null) {
			headers.set("X-AUTH-TOKEN", accessToken);
		}
		ResponseEntity<T> resultado = null;
		
		HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
		try{
			resultado = restTemplate.exchange(url, method, requestEntity, responseType, param);
		}catch(Throwable e){
			int statusCode = ((HttpRetryException)e.getCause()).responseCode();
			return new ResponseEntity<T>(HttpStatus.valueOf(statusCode));
		}
		return resultado;
	}

	protected String toJson(Map<String, Object> param) {
		String jsonResp = null;
		try {
			jsonResp = objectMapper.writeValueAsString(param);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return jsonResp;
	}

	protected <T> T fromJson(String jsonString, Class<T> type) {
		T ob = null;
		try {
			ob = new ObjectMapper().readValue(jsonString, type);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ob;
	}

	protected <T> T getAdvancedSearchfromJson(String jsonString, TypeReference<T> type) {

		T ob = null;
		try {
			ob = new ObjectMapper().readValue(jsonString, type);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ob;
	}

	protected Object processSpEL(String expressionString, Class clazz) {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("body", body);
		context.setVariable("param", param);
		context.setVariable("accessToken", accessToken);
		Expression expression = parser.parseExpression(expressionString);
		if (clazz == null) {
			return expression.getValue(context);
		} else {
			return expression.getValue(context, clazz);

		}
	}

}
