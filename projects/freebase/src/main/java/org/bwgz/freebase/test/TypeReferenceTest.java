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

package org.bwgz.freebase.test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.bwgz.freebase.model.Person;
import org.bwgz.freebase.model.Quotation;
import org.bwgz.freebase.query.MQLMultipleResultResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

public class TypeReferenceTest<T> {
	private static final String personQuery =
		"[{"									+
			  "\"type\": \"/people/person\","	+
			  "\"id\": null,"					+
			  "\"name\": null,"					+
			  "\"gender\": {"					+
			    "\"type\": \"/people/gender\","	+
			    "\"id\": null,"					+
			    "\"name\": \"Male\""			+
			    "},"							+
			  "\"limit\": 3"					+
			  "}]";

	private static final String quotationQuery =
		"[{"+
			  "\"type\": \"/media_common/quotation\","	+
			  "\"id\": null,"							+
			  "\"name\": null,"							+
			  "\"author\": [{"							+
			      "\"type\": \"/people/person\","		+
			      "\"id\": null,"						+
			      "\"name\": null,"						+
			      "\"gender\": {"						+
			        "\"type\": \"/people/gender\","		+
			        "\"id\": null,"						+
			        "\"name\": null"					+
			        "}"									+
			      "}],"									+
			  "\"limit\": 3"							+
			  "}]";

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
	
	public void query(String query, Class<T> clazz) {
		T[] results = getResults(query, clazz);
		for (T result : results) {
	    	System.out.printf("result: %s\n", result);
		}
	}
	
	public static void main(String[] args) {
		TypeReferenceTest<Person> test1 = new TypeReferenceTest<Person>();
		test1.query(personQuery, Person.class);

		TypeReferenceTest<Quotation> test2 = new TypeReferenceTest<Quotation>();
		test2.query(quotationQuery, Quotation.class);
	}
}
