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
package org.bwgz.google.freebase.cli;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.bwgz.google.api.services.freebase.util.TopicUtil;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Base64;
import com.google.api.services.freebase.model.TopicLookup;
import com.google.api.services.freebase.model.TopicValue;
import com.google.api.services.freebase.model.TopicValue.Citation;

public class GenerateAuthorsTest {
	private static final String COMMON_TYPE_DESCRIPTION		= "/common/topic/description";
	private static final String PEOPLE_PERSON_QUOTATIONS	= "/people/person/quotations";
	private static final String TYPE_OBJECT_NAME			= "/type/object/name";

	public static void main(String[] args) {
	    JsonFactory jsonFactory = new JacksonFactory();
		JsonParser parser;
		ArrayMap<String, String> images = null;
		
		try {
			parser = jsonFactory.createJsonParser(new FileReader("authors-image.json"));
			images = parser.parse(ArrayMap.class, null);
			System.out.println(images);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			parser = jsonFactory.createJsonParser(new FileReader("authors.json"));
			TopicLookup[] persons = parser.parse(TopicLookup[].class, null);
			System.out.println(persons);
			
			for (TopicLookup person : persons) {
				String id = person.getId();
				System.out.println(id);
				String name = TopicUtil.getFirstPropertyValue(person, TYPE_OBJECT_NAME).toString();
				System.out.println(name);
				String description = TopicUtil.getFirstPropertyValue(person, COMMON_TYPE_DESCRIPTION).toString();
				System.out.println(description);
				Citation citation = (Citation) TopicUtil.getPropertyValue(person, COMMON_TYPE_DESCRIPTION, 0, "citation");
				System.out.println(citation.getProvider());
				System.out.println(citation.getStatement());
				System.out.println(citation.getUri());

				List<TopicValue> quotaions = TopicUtil.getPropertyValues(person, PEOPLE_PERSON_QUOTATIONS);
				for (TopicValue quotation : quotaions) {
					String mid = TopicUtil.getPropertyValue(quotation, "id").toString();
					System.out.println(mid);
					String text = TopicUtil.getPropertyValue(quotation, "text").toString();
					System.out.println(text);
				}
				
				byte[] image = null;
				if (images != null) {
					String string = images.get(id);
					if (string != null) {
						image = Base64.decodeBase64(string);
					}
				}
				System.out.println(image);
				
				System.out.println("----------------------------------------------");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
