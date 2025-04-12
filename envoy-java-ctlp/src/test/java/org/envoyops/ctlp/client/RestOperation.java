package org.envoyops.ctlp.client;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.boot.jackson.JsonMixinModule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class RestOperation {

	protected RestClient restClient;

	protected ObjectMapper objectMapper = new ObjectMapper();

	public RestOperation(RestClient restClient) {
		this.restClient = restClient;
		this.configureObjectMapper(this.objectMapper);
	}

	protected void configureObjectMapper(ObjectMapper objectMapper) {
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

		objectMapper.registerModule(new Jdk8Module());
		objectMapper.registerModule(new ParameterNamesModule());
		objectMapper.registerModule(new JsonComponentModule());
		objectMapper.registerModule(new JsonMixinModule());
		objectMapper.registerModule(new JavaTimeModule());
	}

	protected <T> T doGet(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, ParameterizedTypeReference<T> responseType) {

		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.get().uri(uri).retrieve().body(responseType);

	}

	protected <T> T doPost(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, Object requestBody,
			ParameterizedTypeReference<T> responseType) {

		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.post()
			.uri(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(requestBody)
			.retrieve()
			.body(responseType);
	}

	protected <T> T doPut(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, Object requestBody,
			ParameterizedTypeReference<T> responseType) {

		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.put()
			.uri(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(requestBody)
			.retrieve()
			.body(responseType);
	}

	protected <T> T doPut(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, Object requestBody, Class<T> responseType) {

		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.put()
			.uri(uri)
			.contentType(MediaType.APPLICATION_JSON)
			.body(requestBody)
			.retrieve()
			.body(responseType);
	}

	protected <T> T doDelete(String path, Map<String, Object> uriVariables,
			LinkedMultiValueMap<String, String> queryParams, ParameterizedTypeReference<T> responseType) {
		var uri = UriComponentsBuilder.fromPath(path)
			.uriVariables(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
			.queryParams(queryParams)
			.build()
			.toUriString();

		return this.restClient.delete().uri(uri).retrieve().body(responseType);
	}

	public <T> LinkedMultiValueMap<String, String> toQueryParam(T obj) {
		return this.objectMapper.convertValue(obj, new TypeReference<>() {
		});
	}

}
