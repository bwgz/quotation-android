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

import java.util.List;

import org.bwgz.freebase.model.Person;
import org.bwgz.freebase.model.Quotation;
import org.bwgz.freebase.persistence.EntityManager;
import org.bwgz.freebase.persistence.EntityManagerFactory;
import org.bwgz.freebase.persistence.Persistence;
import org.bwgz.freebase.persistence.TypedQuery;
import org.bwgz.freebase.persistence.criteria.CriteriaBuilder;
import org.bwgz.freebase.persistence.criteria.CriteriaQuery;

/*
 * EntityManager em = ...;
 * CriteriaBuilder cb = em.getCriteriaBuilder();
 * CriteriaQuery<Pet> cq = cb.createQuery(Pet.class);
 * Root<Pet> pet = cq.from(Pet.class);
 * cq.select(pet);
 * TypedQuery<Pet> q = em.createQuery(cq);
 * List<Pet> allPets = q.getResultList();
 */

public class CriteriaTestOne {
	public static <T> void dump(List<T> list) {
		if (list != null) {
			for (T result : list) {
				System.out.printf("result: %s\n", result);
			}
		}
	}
	
	public static <T> void test(Class<T> clazz) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("https", "www.googleapis.com", "/freebase/v1/mqlread/");
		EntityManager em = emf.createEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		TypedQuery<T> tq = em.createQuery(cq);
		List<T> list = tq.getResultList();
		
		dump(list);
	}
	
	
	public static void main(String[] args) {
		test(Person.class);
		test(Quotation.class);
	}
}
