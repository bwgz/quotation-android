/*
` * Copyright (C) 2013 bwgz.org
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.bwgz.freebase.model.Gender;
import org.bwgz.freebase.model.Person;
import org.bwgz.freebase.query.MQLMultipleResultResponse;
import org.bwgz.freebase.query.MQLProperty;
import org.bwgz.freebase.query.MQLQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

class PersonsResponse extends MQLMultipleResultResponse<Person> {
}

public class PersonTestThree {
	
	public static void main(String[] args) {
		Map<Class<?>, MQLProperty[]> map = new HashMap<Class<?>, MQLProperty[]>();

		MQLProperty[] directives = new MQLProperty[] {
				new MQLProperty("limit", new Integer(3))
		};
		map.put(Person.class, directives);

		Gender gender = new Gender();
		gender.setName("Male");
		Person person = new Person();
		person.setGender(gender);
		
		MQLQueryBuilder builder = new MQLQueryBuilder(MQLQueryBuilder.PROPERTY_PRETTY);
		String mql = builder.createQuery(Person[].class, map, person);
		System.out.println(mql);
		
		Map<String, String> variables = new HashMap<String, String>();
		variables.put("query", mql);

		for(;;) {
			try {
				URI uri = new URI("https", "www.googleapis.com", "/freebase/v1/mqlread/", Util.createQuery(variables), null);
	
				RestTemplate restTemplate = new RestTemplate();
		        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());
		        
		    	ResponseEntity<PersonsResponse> entity = restTemplate.getForEntity(uri, PersonsResponse.class);
		        
		    	PersonsResponse response = entity.getBody();
		    	Person[] persons = response.getResult();
		        for (Person p : persons) {
		        	System.out.printf("person: %s  gender: %s\n", p.getName(), p.getGender().getName());
		        }
		        System.out.printf("returned: %s  cursor: %s\n", persons.length, response.getCursor());
		        
		        if (response.getCursor() == null || response.getCursor().equals("false")) {
		        	break;
		        }
		        
				variables.put("cursor", response.getCursor());
				
				break;
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
}