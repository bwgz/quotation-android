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

import org.bwgz.freebase.persistence.criteria.CriteriaBuilder;
import org.bwgz.freebase.persistence.criteria.CriteriaQuery;

public interface EntityManager {
	public Query createQuery(String query);
	public Query createQuery(String query, Class<?> resultClass);

	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery);
	public CriteriaBuilder getCriteriaBuilder();
	
	<T> T find(Class<T> entityClass, Object primaryKey);
}
