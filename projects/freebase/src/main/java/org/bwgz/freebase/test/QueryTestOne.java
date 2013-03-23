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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bwgz.freebase.model.Person;
import org.bwgz.freebase.persistence.EntityManager;
import org.bwgz.freebase.persistence.EntityManagerFactory;
import org.bwgz.freebase.persistence.Persistence;
import org.bwgz.freebase.persistence.Query;

public class QueryTestOne {
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
	
	public static <T> void dump(List<T> list) {
		if (list != null) {
			for (T result : list) {
				System.out.printf("result: %s\n", result);
			}
		}
	}
	
	public static void test1(EntityManager em, String query) {
		Query q = em.createQuery(query);
		List<?> list = q.getResultList();
		dump(list);
	}
	
	public static void test2(EntityManager em, String query, Class<?> clazz) {
		Query q = em.createQuery(query, clazz);
		List<?> list = q.getResultList();
		dump(list);
	}
	
	public static void test3(EntityManager em, Class<?> clazz, String id) {
		Object entity = em.find(clazz, id);
		System.out.printf("entity: %s\n", entity);
	}
	
	public static void main(String[] args) {
		String key = null;
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("key", key);
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("https", "www.googleapis.com", "/freebase/v1/mqlread/", properties);
		EntityManager em = emf.createEntityManager();
		  
		test1(em, personQuery);
		test2(em, personQuery, Person.class);
		test3(em, Person.class, "/en/robert_cook");
	}
}
