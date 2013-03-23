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
import java.util.Map;

import org.bwgz.freebase.model.Gender;
import org.bwgz.freebase.model.Person;
import org.bwgz.freebase.query.MQLProperty;
import org.bwgz.freebase.query.MQLQueryBuilder;

public class PersonTestOne {

	public static void main(String[] args) {
		Map<Class<?>, MQLProperty[]> map = new HashMap<Class<?>, MQLProperty[]>();

		MQLProperty[] directives = new MQLProperty[] {
				new MQLProperty("limit", new Integer(3))
		};
		map.put(Person.class, directives);

		MQLQueryBuilder builder = new MQLQueryBuilder(
				MQLQueryBuilder.PROPERTY_PRETTY);
		String mql = builder.createQuery(Person[].class, map);
		System.out.println(mql);
		
		Gender gender = new Gender();
		gender.setName("Male");
		Person person = new Person();
		person.setGender(gender);
		mql = builder.createQuery(Person[].class, map, person);
		System.out.println(mql);
	}
}