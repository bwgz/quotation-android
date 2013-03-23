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

package org.bwgz.freebase.persistence;

import java.util.Map;

import org.bwgz.freebase.persistence.impl.EntityManagerFactoryImpl;

public class Persistence {
	public static EntityManagerFactory createEntityManagerFactory(String scheme, String authority, String path, Map<String, Object> properties) {
		return new EntityManagerFactoryImpl(scheme, authority, path, properties);
	}
	
	public static EntityManagerFactory createEntityManagerFactory(String scheme, String authority, String path) {
		return createEntityManagerFactory(scheme, authority, path, null);
	}
}
