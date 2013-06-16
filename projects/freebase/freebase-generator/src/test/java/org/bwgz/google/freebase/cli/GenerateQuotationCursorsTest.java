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

import org.bwgz.google.freebase.cache.FreebaseCursorCache.FreebaseCursor;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;

public class GenerateQuotationCursorsTest {
	public static void main(String[] args) {
	    JsonFactory jsonFactory = new JacksonFactory();
		try {
			JsonParser parser = jsonFactory.createJsonParser(new FileReader("quotation-mid-cursors.json"));
			FreebaseCursor[] cursors = parser.parse(FreebaseCursor[].class, null);
			System.out.println(cursors);
			
			for (FreebaseCursor cursor : cursors) {
				System.out.printf("offset: %d  length: %d  cursor: %s\n", cursor.getOffset(), cursor.getLength(), cursor.getCursor());
				System.out.println("----------------------------------------------");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
