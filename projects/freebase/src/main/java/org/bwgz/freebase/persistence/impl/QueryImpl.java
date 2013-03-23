/*
 * Copyright (C) 2013 bwgz.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bwgz.freebase.persistence.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bwgz.freebase.persistence.TypedQuery;
import org.bwgz.freebase.query.MQLMultipleResultResponse;
import org.bwgz.freebase.test.Util;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
	
public class QueryImpl<T> implements TypedQuery<T> {
	private EntityManagerImpl entityManager;
	private String query;
	private Class<T> resultClass;
	
	public QueryImpl(EntityManagerImpl entityManager, String query, Class<T> resultClass) {
		this.resultClass = resultClass;
		this.query = query;
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public QueryImpl(EntityManagerImpl entityManager, String query) {
		this(entityManager, query, (Class<T>) Object.class);
	}

	private T[] getResults() {
		T[] results = null;
		
        TypeReference<MQLMultipleResultResponse<T>> typeReference = new TypeReference<MQLMultipleResultResponse<T>>() {  
        	public Type getType() { 
        		return  ParameterizedTypeImpl.make(MQLMultipleResultResponse.class, new Type[] { resultClass }, null);
        	}
        };

        Map<String, String> variables = new HashMap<String, String>();
		variables.put("query", query);
		
		Map<String, Object> properties = entityManager.getProperties();
		if (properties != null) {
			for (String key : properties.keySet()) {
				Object value = properties.get(key);
				
				if (value != null) {
					variables.put(key, value.toString());
				}
			}
		}
		
		try {
			URI uri = new URI(entityManager.getScheme(), entityManager.getAuthority(), entityManager.getPath(), Util.createQuery(variables), null);
			
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
	
	@Override
	public List<T> getResultList() {
		List <T> list = null;
		
		T[] results = getResults();
		if (results != null) {
			list = Arrays.asList(results);
		}
		
		return list;
	}

	@Override
	public T getSingleResult() {
	    List<T> result = getResultList();
	    
	    return (result != null && !result.isEmpty()) ? (T) result.get(0) : null;
	}
	
	@SuppressWarnings("unchecked")
	public QueryImpl<T> setResultClass(Class<?> resultClass) {
		this.resultClass = (Class<T>) resultClass;
		
		return this;
	}

}
