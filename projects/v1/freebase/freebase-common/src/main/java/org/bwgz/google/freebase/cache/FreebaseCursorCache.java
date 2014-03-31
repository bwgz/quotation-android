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
package org.bwgz.google.freebase.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

public class FreebaseCursorCache {
	static public class FreebaseCursor {
		@Key
		private int offset;
		@Key
		private int length;
		@Key
		private String cursor;
		
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}
		public int getLength() {
			return length;
		}
		public void setLength(int length) {
			this.length = length;
		}
		public String getCursor() {
			return cursor;
		}
		public void setCursor(String cursor) {
			this.cursor = cursor;
		}
	}
	
	private static FreebaseCursorCache instance = null;
	
	public static synchronized FreebaseCursorCache getInstance(InputStream stream) throws IOException {
		if (instance == null) {
			instance = new FreebaseCursorCache(stream);
		}
		
		return instance;
	}
	
	private List<FreebaseCursor> cursors = new ArrayList<FreebaseCursor>();
	
	FreebaseCursorCache(InputStream stream) throws IOException {
	    JsonFactory jsonFactory = new JacksonFactory();
		JsonParser parser = jsonFactory.createJsonParser(stream);
		cursors = Arrays.asList(parser.parse(FreebaseCursor[].class, null));
	}

	private FreebaseCursor search(List<FreebaseCursor> cursors, int key, int imin, int imax) {
		
		// calculate midpoint to cut set in half
		int imid = imin + ((imax - imin) / 2);
		FreebaseCursor cursor = cursors.get(imid);
		
		if (cursor.getOffset() > key) {
	        // key is in lower subset
	        return search(cursors, key, imin, imid-1);
		}
		else if (cursor.getOffset() + (cursor.getLength() - 1) < key) {
			// key is in upper subset
			return search(cursors, key, imid + 1, imax);
		}
		else {
			return cursor;
		}
	}

	public FreebaseCursor getCursor(int value) {
		return search(cursors, value, 0, cursors.size() - 1);
	}

	public int getMaxResults() {
		int size = cursors.size();
		return (size != 0) ? cursors.get(size - 1).getOffset() + cursors.get(size - 1).getLength() : 0;
	}
}

