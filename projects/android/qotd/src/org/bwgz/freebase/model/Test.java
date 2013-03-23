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

	protected String toString(int tab) {
		String format = tab != 0 ? "%" + (tab * 2) + "s" : "%s";
        String pad = String.format(format, tab != 0 ? " " : "");
        
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append(super.toString()).append(": {\n");
        
        tab++;
		format = tab != 0 ? "%" + (tab * 2) + "s" : "%s";
        pad = String.format(format, tab != 0 ? " " : "");
       
		for (Method method : getClass().getMethods()) {
			String name = method.getName();
			if (name.startsWith("get")) {
				Object value = null;
	
				try {
					method = getClass().getMethod(name);
					if (method != null) {
						value = method.invoke(this);
						
						sb.append(pad).append(String.format("%s: ", name));

						if (value == null) {
							sb.append("null\n");
						}
						else if (value.getClass().isArray()) {
							sb.append(String.format("%s\n", value.toString()));
							
							for (Object item : (Object[]) value) {
								sb.append((item instanceof Test) ? ((Test) item).toString(tab + 1) : item.toString());
							}
						}
						else {
							sb.append(String.format("%s", (value instanceof Test) ? ((Test) value).toString(tab) : value.toString() + "\n"));
						}
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
			}
		}
		sb.append(pad).append("}\n");

        return sb.toString();
	}
	
	@Override
	public String toString() {
		return toString(0);
	}
}
