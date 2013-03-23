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

package org.bwgz.freebase.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract public class Test implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": { ");
       
		for (Method method : getClass().getMethods()) {
			String name = method.getName();
			if (name.startsWith("get")) {
				Object value = null;
	
				try {
					method = getClass().getMethod(name);
					if (method != null) {
						value = method.invoke(this);
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				sb.append(String.format("%s: %s; ", name, value != null ? value.toString() : null));
			}
		}
        sb.append("}");


        return sb.toString();
	}
}
