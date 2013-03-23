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

package org.bwgz.freebase.persistence.criteria.impl;

import java.lang.reflect.Array;

import org.bwgz.freebase.persistence.criteria.CriteriaQuery;
import org.bwgz.freebase.query.MQLQueryBuilder;

public class CriteriaQueryImpl<T> implements CriteriaQuery<T> {
	private Class<T> resultClass;

	public CriteriaQueryImpl() {
	}

	public CriteriaQueryImpl(Class<T> resultClass) {
        this.resultClass = resultClass;
	}

	public Class<T> getResultClass() {
		return resultClass;
	}

	public void setResultClass(Class<T> resultClass) {
		this.resultClass = resultClass;
	}

    public String toString() {
		String query = null;
		
		MQLQueryBuilder builder = new MQLQueryBuilder(MQLQueryBuilder.PROPERTY_PRETTY);
		query = builder.createQuery(Array.newInstance(resultClass, 0).getClass());
		
		return query;
    }

}
