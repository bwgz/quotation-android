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

package org.bwgz.freebase.query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Stack;

import org.bwgz.freebase.annotation.FBEntity;
import org.bwgz.freebase.annotation.FBProperty;

public class MQLQueryBuilder {
	static private final String OPEN_SQUARE = "[";
	static private final String CLOSE_SQUARE = "]";
	static private final String OPEN_BRACKET = "{";
	static private final String CLOSE_BRACKET = "}";
	static private final String EMPTY = "";
	static private final String SPACE = " ";
	static private final String QUOTE = "\"";
	static private final String COMMA = ",";
	static private final String NEW_LINE = "\n";
	
	static public int PROPERTY_COMPACT	= 0;
	static public int PROPERTY_PRETTY	= 1;
	
	private String space = EMPTY;
	private String new_line = EMPTY;
	
	public MQLQueryBuilder(int property) {
		if (property == PROPERTY_COMPACT) {
			space = EMPTY;
			new_line = EMPTY;
		}
		else if (property == PROPERTY_PRETTY) {
			space = SPACE;
			new_line = NEW_LINE;
		}
	}

	public MQLQueryBuilder() {
		this(PROPERTY_COMPACT);
	}
	
	private String indent(int tab) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < tab * 2; i++) {
			buffer.append(space);
		}
		return buffer.toString();
	}
	
	private String quote(String string) {
		return new StringBuffer().append(QUOTE).append(string).append(QUOTE).toString();
	}

	private Object getValueFromObject(Class<?> clazz, Object object, Field field) {
		//System.out.printf("clazz: %s  object: %s  field: %s\n", clazz, object, field);
		Object value = null;
		String name = "get".concat(field.getName()).toLowerCase();
		
		Method method = null;
		
		for (Method m : clazz.getMethods()) {
			if (m.getName().toLowerCase().equals(name)) {
				try {
					method = clazz.getMethod(m.getName());
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		
		if (method != null) {
			try {
				value = method.invoke(object);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		return value;
	}

	private String createQuery(Class<?> clazz, Map<Class<?>, MQLProperty[]> map, Object object, Stack<Class<?>> stack, int tab) {
		StringBuffer sb = new StringBuffer();
		String indent = indent(tab);
		System.out.printf("%sclazz: %s  object: %s\n", indent, clazz, object);

		if (clazz.isArray()) {
			sb.append(String.format("%s", OPEN_SQUARE));
			sb.append(createQuery(clazz.getComponentType(), map, object, stack, tab + 1));
			sb.append(String.format("%s", CLOSE_SQUARE));
		}
		else {
			FBEntity fbEntity = clazz.getAnnotation(FBEntity.class);

			if (fbEntity != null) {
				stack.push(clazz);
				boolean next = false;
				sb.append(String.format("%s%s", OPEN_BRACKET, new_line));
				
				for (Field field : clazz.getDeclaredFields()) {
					Class<?> type = field.getType();
					if (!stack.contains(type.isArray() ? type.getComponentType() : type)) {
						FBProperty fbProperty = field.getAnnotation(FBProperty.class);
						if (fbProperty != null) {
							String name = quote((fbProperty.property_name().length() != 0) ? fbProperty.property_name() : field.getName());
							String value = null;
							
							if (type.isArray()) {
								value = createQuery(type, map, object != null ? getValueFromObject(clazz, object, field) : null, stack, tab + 1);
							}
							else {
								fbEntity = type.getAnnotation(FBEntity.class);
								if (fbEntity != null) {
									value = createQuery(type, map, object != null ? getValueFromObject(clazz, object, field) : null, stack, tab + 1);
								}
								else {
									if (object != null) {
										Object o = getValueFromObject(clazz, object, field);
										if (o != null) {
											value = quote(o.toString());
										}
									}
		
									if (value == null) {
										value = fbProperty.property_value().length() != 0 ? quote(fbProperty.property_value()) : null;
									}
								}
							}
							
							sb.append(String.format("%s%s%s:%s%s", next ? (COMMA + new_line) : EMPTY, indent, name, space, value));
							next = true;
						}
					}
				}
				
				if (map != null) {
					MQLProperty[] directives = map.get(clazz);
					if (directives != null) {
						for (MQLProperty directive : directives) {
							String name = quote(directive.getName());
							String value = directive.getValue() != null ? directive.getValue() instanceof String ? quote(directive.getValue().toString()) : directive.getValue().toString() : null;
							sb.append(String.format("%s%s%s:%s%s", next ? (COMMA + new_line) : EMPTY, indent, name, space, value));
							next = true;
						}
					}
				}
				
				sb.append(String.format("%s%s%s", new_line, indent, CLOSE_BRACKET));
				stack.pop();
			}
		}
		
		return sb.toString();
	}

	public String createQuery(Class<?> clazz, Map<Class<?>, MQLProperty[]> map, Object object) {
		return createQuery(clazz, map, object, new Stack<Class<?>>(), 0);
	}
	
	public String createQuery(Class<?> clazz, Map<Class<?>, MQLProperty[]> map) {
		return createQuery(clazz, map, null);
	}
	
	public String createQuery(Class<?> clazz) {
		return createQuery(clazz, null);
	}
}
