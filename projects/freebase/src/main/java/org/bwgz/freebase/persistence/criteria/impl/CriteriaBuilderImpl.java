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

import org.bwgz.freebase.persistence.criteria.CriteriaBuilder;
import org.bwgz.freebase.persistence.criteria.CriteriaQuery;

public class CriteriaBuilderImpl implements CriteriaBuilder {

	@Override
	public <T> CriteriaQuery<T> createQuery() {
        return new CriteriaQueryImpl<T>();
	}

	@Override
	public <T> CriteriaQuery<T> createQuery(Class<T> resultClass) {
        return new CriteriaQueryImpl<T>(resultClass);
	}

	public void compile() {
	}

}
