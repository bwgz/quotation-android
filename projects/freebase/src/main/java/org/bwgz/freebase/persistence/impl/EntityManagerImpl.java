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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.bwgz.freebase.persistence.EntityManager;
import org.bwgz.freebase.persistence.Query;
import org.bwgz.freebase.persistence.TypedQuery;
import org.bwgz.freebase.persistence.criteria.CriteriaBuilder;
import org.bwgz.freebase.persistence.criteria.CriteriaQuery;
import org.bwgz.freebase.persistence.criteria.impl.CriteriaBuilderImpl;
import org.bwgz.freebase.persistence.criteria.impl.CriteriaQueryImpl;
import org.bwgz.freebase.query.MQLQueryBuilder;

public class EntityManagerImpl implements EntityManager {
	private String scheme;
	private String authority;
	private String path;
	Map<String, Object> properties;
	
	public EntityManagerImpl(String scheme, String authority, String path, Map<String, Object> properties) {
		this.scheme = scheme;
		this.authority = authority;
		this.path = path;
		this.properties = properties;
	}

	@Override
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		return new QueryImpl<T>(this, ((CriteriaQueryImpl<T>) criteriaQuery).toString(), ((CriteriaQueryImpl<T>) criteriaQuery).getResultClass());
	}

	@Override
	public CriteriaBuilder getCriteriaBuilder() {
		return new CriteriaBuilderImpl();
	}

	@Override
	public Query createQuery(String query) {
		return (Query) new QueryImpl<Object>(this, query);
	}

	@Override
	public Query createQuery(String query, Class<?> resultClass) {
		return (Query) new QueryImpl<Object>(this, query).setResultClass(resultClass);
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		T entity = null;
		
		try {
			 T object = entityClass.newInstance();
			 Method method = entityClass.getMethod("setId", String.class);
			 method.invoke(object, primaryKey);
			 
			 MQLQueryBuilder qb = new MQLQueryBuilder();
			 String query = qb.createQuery(Array.newInstance(entityClass, 0).getClass(), null, object);
		
			 Query q = createQuery(query);
			 entity = (T) q.getSingleResult();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return entity;
	}
}
