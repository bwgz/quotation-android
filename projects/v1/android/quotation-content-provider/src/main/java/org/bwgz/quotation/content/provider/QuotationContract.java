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
package org.bwgz.quotation.content.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings("unused")
public final class QuotationContract {
	public static final String AUTHORITY = "org.bwgz.quotation";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	public static final String DATABASE = "quotation";
	
	protected interface StatusColumns {
		public static final String MODIFIED		= "modified";
	}

	protected interface CacheColumns {
		public static final String EVICTABLE	= "evictable";
	}
	
	protected interface LanguageColumns {
		public static final String LANGUAGE	= "language";
	}

	protected interface QuotationColumns {
		public static final String QUOTATION	= "quotation";
	}

	public static class Quotation implements BaseColumns, StatusColumns, CacheColumns, LanguageColumns, QuotationColumns {
		public static final String TABLE = "quotation";
		
		private Quotation() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Quotation.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
		
		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * quotations.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/quotation";

		/**
		 * The MIME type of a {@link #CONTENT_URI} a single quotation.
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.org.bwgz.freebase.quotation";

	}
	
	protected interface PersonColumns {
		public static final String NAME			= "name";
		public static final String DESCRIPTION	= "description";
		public static final String IMAGE		= "image";
	}

	protected interface CitationColumns {
		public static final String CITATION_PROVIDER	= "citation_provider";
		public static final String CITATION_STATEMENT	= "citation_statement";
		public static final String CITATION_URI			= "citation_uri";
	}
	
	public static class Person implements BaseColumns, StatusColumns, CacheColumns, LanguageColumns, PersonColumns, CitationColumns {
		public static final String TABLE = "person";
		
		private Person() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, TABLE);

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Person.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
		
		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * quotations.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/person";

		/**
		 * The MIME type of a {@link #CONTENT_URI} a single quotation.
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.org.bwgz.freebase.person";

	}
	
	protected interface QuotationPersonColumns {
		public static final String QUOTATION_ID	= "quotation_id";
		public static final String PERSON_ID	= "person_id";
	}

	public static class QuotationPerson implements BaseColumns, StatusColumns, QuotationPersonColumns {
		public static final String TABLE = "quotation_person";
		
		private QuotationPerson() {
		}

		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "quotation/person");

		public static Uri withAppendedId(String id) {
			return Uri.parse(CONTENT_URI + id);
		}

		public static String getId(String uri) {
	    	return uri.substring(Person.CONTENT_URI.toString().length(), uri.length());
		}
		
		public static String getId(Uri uri) {
	    	return getId(uri.toString());
		}
		
		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of
		 * quotations.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/quotation/person";

		/**
		 * The MIME type of a {@link #CONTENT_URI} a single quotation.
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/quotation/person";

	}
}
