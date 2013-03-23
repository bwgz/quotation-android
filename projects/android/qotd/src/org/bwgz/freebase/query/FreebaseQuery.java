package org.bwgz.freebase.query;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public class FreebaseQuery<T> {
	public T[] getResults(String query, final Class<T> clazz) {
		T[] results = null;
		
        TypeReference<MQLMultipleResultResponse<T>> typeReference = new TypeReference<MQLMultipleResultResponse<T>>() {  
        	public Type getType() { 
        		return  ParameterizedTypeImpl.make(MQLMultipleResultResponse.class, new Type[] { clazz }, null);
        	}
        };

        Map<String, String> variables = new HashMap<String, String>();
		variables.put("query", query);
		try {
			URI uri = new URI("https", "www.googleapis.com", "/freebase/v1/mqlread/", Util.createQuery(variables), null);
			
			ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			ClientHttpRequest request = requestFactory.createRequest(uri, HttpMethod.GET);
			ClientHttpResponse response = request.execute();
			ObjectMapper objectMapper = new ObjectMapper();
			MQLMultipleResultResponse<T> body = objectMapper.readValue(response.getBody(), typeReference);
	    	results = body.getResult();
			response.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return results;
	}
	
	public T getResult(String query, final Class<T> clazz) {
		T result = null;
		
        TypeReference<MQLSingleResultResponse<T>> typeReference = new TypeReference<MQLSingleResultResponse<T>>() {  
        	public Type getType() { 
        		return  ParameterizedTypeImpl.make(MQLSingleResultResponse.class, new Type[] { clazz }, null);
        	}
        };

        Map<String, String> variables = new HashMap<String, String>();
		variables.put("query", query);
		try {
			URI uri = new URI("https", "www.googleapis.com", "/freebase/v1/mqlread/", Util.createQuery(variables), null);
			
			ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			ClientHttpRequest request = requestFactory.createRequest(uri, HttpMethod.GET);
			ClientHttpResponse response = request.execute();
			ObjectMapper objectMapper = new ObjectMapper();
			MQLSingleResultResponse<T> body = objectMapper.readValue(response.getBody(), typeReference);
	    	result = body.getResult();
			response.close();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
