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

import java.util.Map;

import org.bwgz.freebase.persistence.EntityManager;
import org.bwgz.freebase.persistence.EntityManagerFactory;

public class EntityManagerFactoryImpl implements EntityManagerFactory {
	private String scheme;
	private String authority;
	private String path;
	private Map<String, Object> properties;
	
	public EntityManagerFactoryImpl(String scheme, String authority, String path, Map<String, Object> properties) {
		this.scheme = scheme;
		this.authority = authority;
		this.path = path;
		this.properties = properties;
	}

	@Override
	public EntityManager createEntityManager() {
		return new EntityManagerImpl(scheme, authority, path, properties);
	}
}
